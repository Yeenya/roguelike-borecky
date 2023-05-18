package cz.cuni.gamedev.nail123.roguelike.world.builders

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.blocks.Floor
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.world.IArea
import cz.cuni.gamedev.nail123.roguelike.world.Area
import cz.cuni.gamedev.nail123.utils.collections.observableMapOf
import kotlinx.collections.immutable.toPersistentMap
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

abstract class AreaBuilder(
        override val size: Size3D = GameConfig.AREA_SIZE,
        val visibleSize: Size3D = GameConfig.VISIBLE_SIZE): IArea {

    // We always set the unknown position as a place for unplaceable entities (such as FogOfWar)
    final override var blocks = observableMapOf<Position3D, GameBlock>(Position3D.unknown() to Floor())

    override fun get(position: Position3D) = blocks[position]

    var player = Player()

    init {
        blocks.onAddIndexed { pos, block -> block.position = pos }
    }

    fun build(): Area {
        val area = Area(blocks.toPersistentMap(), visibleSize, size, player)

        return area.apply {
            entities.forEach { it.init() }
            // This is here to update autotiling
            blocks.values.forEach { it.updateTileMap() }
        }
    }

    abstract fun create(): AreaBuilder
}