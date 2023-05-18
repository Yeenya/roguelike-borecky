package cz.cuni.gamedev.nail123.roguelike.entities.objects

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Interactable
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

class Door: GameEntity(GameTiles.CLOSED_DOOR), Interactable {
    val isOpenProperty = createPropertyFrom(false)
    var isOpen by isOpenProperty.asDelegate()

    override val blocksMovement: Boolean
        get() = !isOpen
    override val blocksVision: Boolean
        get() = !isOpen

    init {
        isOpenProperty.onChange { tile = if (isOpen) GameTiles.OPEN_DOOR else GameTiles.CLOSED_DOOR }
    }

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(InteractionType.BUMPED) { isOpen = !isOpen }
    }
}