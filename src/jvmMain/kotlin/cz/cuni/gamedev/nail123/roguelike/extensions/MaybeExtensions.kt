package cz.cuni.gamedev.nail123.roguelike.extensions

import org.hexworks.cobalt.datatypes.Maybe

// The Zircon library contains a Maybe class, which I think is an anti-pattern in Kotlin
/** Converts the Maybe to a nullable class */
val <T> Maybe<T>.asNullable: T?
    get() = if (isPresent) get() else null