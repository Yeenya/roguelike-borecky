package cz.cuni.gamedev.nail123.utils.collections

import org.hexworks.cobalt.core.behavior.DisposeState
import org.hexworks.cobalt.core.behavior.NotDisposed
import org.hexworks.cobalt.events.api.*

/**
 * Own implementation of observable list, notably different in these aspects from DefaultListProperty of Cobalt:
 *  - triggers the events immediately in the original thread
 *  - contains specific listeners for element add / remove
 *  - doesn't implicitly use persistent list (the events don't have an immutable value as old/new, but sometimes you
 *    don't need that)
 */
class ObservableMap<K, V>(private val map: MutableMap<K, V> = mutableMapOf()): MutableMap<K, V> {
    data class ElementAddedAt<K, V>(override val emitter: ObservableMap<K, V>, val index: K, val element: V) : Event
    data class ElementRemovedAt<K, V>(override val emitter: ObservableMap<K, V>, val fromIndex: K, val element: V) : Event
    data class Changed<K, V>(override val emitter: ObservableMap<K, V>) : Event

    object Undisposable: Subscription {
        override val disposeState: DisposeState
            get() = NotDisposed
        override fun dispose(disposeState: DisposeState) {}
    }

    override val size
        get() = map.size

    val addListeners = ArrayList<(ElementAddedAt<K, V>) -> CallbackResult>()
    val removeListeners = ArrayList<(ElementRemovedAt<K, V>) -> CallbackResult>()
    val changeListeners = ArrayList<(Changed<K, V>) -> CallbackResult>()

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
    private fun added(index: K, element: V) {
        addListeners.removeIf { fn -> fn(ElementAddedAt(this, index, element)) == DisposeSubscription }
        valueChanged()
    }
    private fun removed(fromIndex: K, element: V) {
        removeListeners.removeIf { fn -> fn(ElementRemovedAt(this, fromIndex, element)) == DisposeSubscription }
        valueChanged()
    }

    // Listening for events

    private fun onAddInner(fn: (ElementAddedAt<K, V>) -> Unit): Subscription {
        addListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onAddIndexed(fn: (K, V) -> Unit) = onAddInner { (_, index, element) -> fn(index, element) }
    fun onAdd(fn: (V) -> Unit) = onAddInner { (_, _, element) -> fn(element) }

    private fun onRemoveInner(fn: (ElementRemovedAt<K, V>) -> Unit): Subscription {
        removeListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onRemoveIndexed(fn: (K, V) -> Unit) = onRemoveInner { (_, index, element) -> fn(index, element) }
    fun onRemove(fn: (V) -> Unit) = onRemoveInner { (_, _, element) -> fn(element) }

    private fun onChangeInner(fn: (Changed<K, V>) -> Unit): Subscription {
        changeListeners.add {
            fn(it)
            KeepSubscription
        }
        return Undisposable
    }
    fun onChange(fn: () -> Unit) = onChangeInner { (_) -> fn() }

    /*
     * DELEGATION
     *
     * These function are required by the Map<K, V> interface and provide access to elements.
     */

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries
    override val keys: MutableSet<K>
        get() = map.keys
    override val values: MutableCollection<V>
        get() = map.values

    override fun containsKey(key: K) = map.containsKey(key)
    override fun containsValue(value: V) = map.containsValue(value)
    override operator fun get(key: K): V? = map[key]
    override fun isEmpty() = map.isEmpty()

    /*
     * MUTABILITY
     *
     * Functions that provide mutability, required by the MutableMap<K, V> interface
     */

    override fun clear() = keys.forEach { remove(it) }
    override fun put(key: K, value: V) = map.put(key, value).also { added(key, value) }
    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }
    override fun remove(key: K) = map.remove(key).also { if (it != null ) removed(key, it) }

    /*
     * OVERRIDE DEFAULT OBJECT METHODS
     */
    override fun equals(other: Any?) = other is ObservableMap<*, *> && other.map == map
    override fun hashCode() = map.hashCode()
    override fun toString() = "ObservableMap($map)"
}

fun <K, V> observableMapOf(vararg elements: Pair<K, V>) = ObservableMap(mutableMapOf(*elements))
fun <K, V> ObservableMap<K, V>.withAddListener(fn: (V) -> Unit): ObservableMap<K, V> = apply { onAdd(fn) }