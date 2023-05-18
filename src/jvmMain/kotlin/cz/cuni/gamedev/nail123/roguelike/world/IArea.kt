package cz.cuni.gamedev.nail123.roguelike.world

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.extensions.allPositionsShuffled
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

/**
 * Interface of common features of AreaBuilder (containing incomplete, in-progress areas) and Area.
 */
interface IArea {
    val size: Size3D
    val width
        get() = size.xLength
    val height
        get() = size.yLength
    val allPositions
        get() = size.fetchPositions()
    val blocks: Map<Position3D, GameBlock>

    val entities: List<GameEntity>
        get() = blocks.values.flatMap { it.entities }

    operator fun get(position: Position3D): GameBlock?

    fun addEntity(entity: GameEntity, position: Position3D) {
        (blocks[position] ?: error("Adding entity to non-existing position")).entities.add(entity)
    }
    fun removeEntity(entity: GameEntity) {
        blocks[entity.position]?.entities?.remove(entity)
    }

    fun addAtEmptyPosition(entity: GameEntity, offset: Position3D, size: Size3D): Boolean {
        val emptyPosition = size.allPositionsShuffled()
                .map { pos -> pos + offset }
                .filter { pos ->
                    this[pos]?.blocksMovement == false
                }.firstOrNull()

        if (emptyPosition != null) addEntity(entity, emptyPosition)
        return emptyPosition != null
    }
}