package cz.cuni.gamedev.nail123.roguelike.entities

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interaction
import cz.cuni.gamedev.nail123.roguelike.extensions.shift
import cz.cuni.gamedev.nail123.roguelike.world.Direction
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Tile

abstract class MovingEntity(
        tile: Tile = Tile.empty(),
        sortingLayer: SortingLayer = SortingLayer.CHARACTER): GameEntity(tile, sortingLayer) {

    enum class MovementResult {
        MOVED, BUMPED, POSITION_BLOCKED, OUT_OF_BOUNDS;
        val isSuccess: Boolean
            get() = this == MOVED || this == BUMPED
    }

    var lastMovement: Direction? = null

    fun move(direction: Direction): MovementResult {
        lastMovement = direction

        val nextBlock = area[position shift direction] ?: return MovementResult.OUT_OF_BOUNDS

        return if (nextBlock.blocksMovement) {
            if (interaction(this, nextBlock, InteractionType.BUMPED)) MovementResult.BUMPED
            else MovementResult.POSITION_BLOCKED
        } else {
            moveTo(position shift direction)
            interaction(this, nextBlock, InteractionType.STEPPED_ON)
            MovementResult.MOVED
        }
    }

    fun moveTo(targetPosition: Position3D) {
        if (this.position != Position3D.unknown())
            area.removeEntity(this)
        area.addEntity(this, targetPosition)
    }
}
