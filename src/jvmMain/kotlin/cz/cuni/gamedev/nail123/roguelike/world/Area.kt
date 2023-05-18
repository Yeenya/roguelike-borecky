package cz.cuni.gamedev.nail123.roguelike.world

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.extensions.asNullable
import cz.cuni.gamedev.nail123.utils.collections.observableListOf
import kotlinx.collections.immutable.PersistentMap
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea
/**
 * Represents a part of the world that is updated at a time and the player can freely move in.
 */
class Area(startingBlocks: PersistentMap<Position3D, GameBlock>,
           visibleSize: Size3D,
           actualSize: Size3D,
           var player: Player) : BaseGameArea<Tile, GameBlock>(
                visibleSize,
                actualSize,
                initialContents = startingBlocks
            ), IArea {

    lateinit var world: World
    override val size
        get() = actualSize
    val visibleWidth
        get() = visibleSize.xLength
    val visibleHeight
        get() = visibleSize.yLength

    init {
        startingBlocks.forEach { (pos, block) ->
            setBlockAt(pos, block)
        }
    }

    override fun get(position: Position3D): GameBlock? = fetchBlockAt(position).asNullable
    override fun setBlockAt(position: Position3D, block: GameBlock) {
        super.setBlockAt(position, block)
        block.area = this
        block.position = position
    }
}
