package cz.cuni.gamedev.nail123.roguelike.entities.attributes

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity

enum class InteractionType {
    BUMPED, STEPPED_ON
}
/**
 * This describes the interactions interface. Game entities can interact with one another, and this can be specified
 * from both the caller and the callee viewpoints. If both the caller and the callee will define an interaction, only
 * the one specified in the caller will be applied.
 */
interface Interacting {
    fun interactWith(other: GameEntity, type: InteractionType): Boolean
}
interface Interactable {
    fun acceptInteractFrom(other: GameEntity, type: InteractionType): Boolean
}
class InteractionScope(val other: GameEntity, val type: InteractionType) {
    var interacted = false
    inline fun <reified T> withEntity(type: InteractionType, interactionHandler: (T) -> Unit) {
        if (other is T && type == this.type) {
            interactionHandler(other)
            interacted = true
        }
    }
}
fun interactionContext(other: GameEntity, type: InteractionType, scopeFunc: InteractionScope.() -> Unit): Boolean {
    val scope = InteractionScope(other, type)
    scope.scopeFunc()
    return scope.interacted
}

/**
 * Perform an interaction of an entity and a target block. Currently only first-found is performed, but this may change.
 * @return Whether an interaction occured.
 */
fun interaction(source: GameEntity, target: GameBlock, type: InteractionType): Boolean {
    if (source is Interacting) {
        target.entities.forEach {
            if (source.interactWith(it, type)) return true
        }
    }
    target.entities.filterIsInstance<Interactable>().forEach {
        if (it.acceptInteractFrom(source, type)) return true
    }
    return false
}