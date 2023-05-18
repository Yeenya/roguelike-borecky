package cz.cuni.gamedev.nail123.roguelike.blocks

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.world.Area
import cz.cuni.gamedev.nail123.utils.collections.observableListOf
import kotlinx.collections.immutable.persistentMapOf
import org.hexworks.zircon.api.data.BlockTileType
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.base.BaseBlock

open class GameBlock(open var baseTile: Tile): BaseBlock<Tile>(
        emptyTile = Tile.empty(),
        // Tiles not only make a cube, but also act as 3 layers
        // We consider bottom-layer = FLOOR/WALL, content = ENTITIES, top = FOG_OF_WAR
        tiles = persistentMapOf(BlockTileType.BOTTOM to baseTile)) {

    open val blocksMovement: Boolean
        get() = entities.any { it.blocksMovement }
    open val blocksVision: Boolean
        get() = entities.any { it.blocksVision }

    lateinit var area: Area
    var position: Position3D = Position3D.unknown()
        set(value) {
            if (field != value) entities.forEach { it.position = value }
            field = value
        }

    val entities = observableListOf<GameEntity>()

    init {
        entities.onAdd {
            it.block = this
            it.position = this.position
        }
        entities.onChange { updateTileMap() }
    }

    fun updateTileMap() {
        val topEntity = entities.maxByOrNull { it.sortingLayer }
        content = topEntity?.tile ?: baseTile
        bottom = baseTile
    }
}