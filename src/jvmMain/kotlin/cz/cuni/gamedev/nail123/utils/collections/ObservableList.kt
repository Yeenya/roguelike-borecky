package cz.cuni.gamedev.nail123.utils.collections

import org.hexworks.cobalt.core.behavior.DisposeState
import org.hexworks.cobalt.core.behavior.NotDisposed
import org.hexworks.cobalt.events.api.*
import kotlin.collections.ArrayList

/**
 * Own implementation of observable list, notably different in these aspects from DefaultListProperty of Cobalt:
 *  - triggers the events immediately in the original thread
 *  - contains specific listeners for element add / remove
 *  - doesn't implicitly use persistent list (the events don't have an immutable value as old/new, but sometimes you
 *    don't need that)
 */
class ObservableList<T>(private val list: MutableList<T> = ArrayList()): MutableList<T>, RandomAccess {
    data class ElementAddedAt<T>(override val emitter: ObservableList<T>, val index: Int, val element: T) : Event
    data class ElementRemovedAt<T>(override val emitter: ObservableList<T>, val fromIndex: Int, val element: T) : Event
    data class Changed<T>(override val emitter: ObservableList<T>) : Event

    object Undisposable: Subscription {
        override val disposeState: DisposeState
            get() = NotDisposed
        override fun dispose(disposeState: DisposeState) {}
    }

    override val size
        get() = list.size

    val addListeners = ArrayList<(ElementAddedAt<T>) -> CallbackResult>()
    val removeListeners = ArrayList<(ElementRemovedAt<T>) -> CallbackResult>()
    val changeListeners = ArrayList<(Changed<T>) -> CallbackResult>()

    /*
     * LISTENER HANDLING
     *
     * Functions here allow the external world to add (synchronized) listeners when this list is changed
     * Utility functions are provided for easier creation of listeners. In return, they:
     *  - never dispose of the subscription
     *  - immediately unwrap the event class
     */

    // Publishing events

    private fun valueChanged() {
        changeListeners.removeIf { fn -> fn(Changed(this)) == DisposeSubscription }
    }
    private fun added(index: Int, element: T) {
        addListeners.removeIf { fn -> fn(ElementAddedAt(this, index, element)) == DisposeSubscription }
        valueChanged()
    }
    private fun removed(fromIndex: Int, element: T) {
        removeListeners.removeIf { fn -> fn(ElementRemovedAt(this, fromIndex, element)) == DisposeSubscription }
        valueChanged()
    }

    // Listening for events

    private fun onAddInner(fn: (ElementAddedAt<T>) -> Unit): Subscription {
        addListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onAddIndexed(fn: (Int, T) -> Unit) = onAddInner { (_, index, element) -> fn(index, element) }
    fun onAdd(fn: (T) -> Unit) = onAddInner { (_, _, element) -> fn(element) }

    private fun onRemoveInner(fn: (ElementRemovedAt<T>) -> Unit): Subscription {
        removeListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onRemoveIndexed(fn: (Int, T) -> Unit) = onRemoveInner { (_, index, element) -> fn(index, element) }
    fun onRemove(fn: (T) -> Unit) = onRemoveInner { (_, _, element) -> fn(element) }

    private fun onChangeInner(fn: (Changed<T>) -> Unit): Subscription {
        changeListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onChange(fn: () -> Unit) = onChangeInner { (_) -> fn() }

    /*
     * ACCESS DELEGATION
     *
     * These function are required by the List<T> interface and provide access to elements.
     */

    override fun contains(element: T) = list.contains(element)
    override fun containsAll(elements: Collection<T>) = list.containsAll(elements)
    override operator fun get(index: Int) = list[index]
    override fun indexOf(element: T) = list.indexOf(element)
    override fun isEmpty() = list.isEmpty()
    override fun iterator() = list.iterator()
    override fun lastIndexOf(element: T) = list.lastIndexOf(element)
    override fun listIterator() = list.listIterator()
    override fun listIterator(index: Int) = list.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = ObservableList(list.subList(fromIndex, toIndex))

    /*
     * MUTABILITY
     *
     * Functions that provide mutability, similarly to ArrayList<T>
     */

    override operator fun set(index: Int, element: T): T {
        val oldElement = list[index]
        list[index] = element
        removed(index, oldElement)
        added(index, element)
        return oldElement
    }
    override fun add(element: T) = list.add(element).also {
        added(list.size - 1, element)
    }
    override fun add(index: Int, element: T) = list.add(index, element).also {
        added(index, element)
    }
    override fun addAll(elements: Collection<T>) = list.addAll(elements).also {
        elements.forEachIndexed { i, element -> added(elements.size - i - 1, element) }
    }
    override fun addAll(index: Int, elements: Collection<T>) = list.addAll(elements).also {
        elements.forEachIndexed { i, element -> added(index + i, element) }
    }
    override fun clear() = list.indices.reversed().forEach { removeAt(it) }
    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index != -1) {
            removeAt(index)
            true
        } else false
    }
    override fun removeAll(elements: Collection<T>) = elements.map { remove(it) }.any()
    override fun removeAt(index: Int) = list.removeAt(index).also { removed(index, it) }
    override fun retainAll(elements: Collection<T>): Boolean {
        throw NoSuchMethodException("retainAll isn't implemented on ObservableList")
    }

    /*
     * OVERRIDE DEFAULT OBJECT METHODS
     */
    override fun equals(other: Any?) = other is ObservableList<*> && list == other.list
    override fun hashCode() = list.hashCode()
    override fun toString() = "ObservableList($list)"
}

fun <T> observableListOf(vararg elements: T) = ObservableList<T>(elements.toCollection(ArrayList()))
fun <T> ObservableList<T>.withAddListener(fn: (T) -> Unit): ObservableList<T> = apply { onAdd(fn) }