package cz.cuni.gamedev.nail123.roguelike.world

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea
import org.hexworks.zircon.api.graphics.TileImage

/**
 * Decorator pattern (https://en.wikipedia.org/wiki/Decorator_pattern) over BaseGameArea.
 */
abstract class AreaDecorator(visibleSize: Size3D, actualSize: Size3D): BaseGameArea<Tile, GameBlock>(
        visibleSize, actualSize
    ), IArea {

    abstract val innerArea: Area

    override val size
        get() = innerArea.actualSize
    override val actualSize: Size3D
        get() = innerArea.actualSize
    override val visibleSize: Size3D
        get() = innerArea.visibleSize
    override val imageLayers: Sequence<TileImage>
        get() = innerArea.imageLayers
    override val visibleOffset: Position3D
        get() = innerArea.visibleOffset

    override val blocks: Map<Position3D, GameBlock>
        get() = innerArea.blocks
    override val entities: List<GameEntity>
        get() = innerArea.entities

    override fun get(position: Position3D) = blocks[position]
    override fun fetchBlockAt(position: Position3D) = innerArea.fetchBlockAt(position)
    override fun fetchBlocksAt(offset: Position3D, size: Size3D) = innerArea.fetchBlocksAt(offset, size)
    override fun fetchBlocksAtLevel(z: Int) = innerArea.fetchBlocksAtLevel(z)
    override fun hasBlockAt(position: Position3D) = innerArea.hasBlockAt(position)
    override fun setBlockAt(position: Position3D, block: GameBlock) = innerArea.setBlockAt(position, block)
    override fun scrollOneRight(): Position3D = innerArea.scrollOneRight()
    override fun scrollOneLeft(): Position3D = innerArea.scrollOneLeft()
    override fun scrollOneUp(): Position3D = innerArea.scrollOneUp()
    override fun scrollOneDown(): Position3D = innerArea.scrollOneDown()
    override fun scrollOneForward(): Position3D = innerArea.scrollOneForward()
    override fun scrollOneBackward(): Position3D = innerArea.scrollOneBackward()
    override fun scrollRightBy(x: Int): Position3D = innerArea.scrollRightBy(x)
    override fun scrollLeftBy(x: Int): Position3D = innerArea.scrollLeftBy(x)
    override fun scrollUpBy(z: Int): Position3D = innerArea.scrollUpBy(z)
    override fun scrollDownBy(z: Int): Position3D = innerArea.scrollDownBy(z)
    override fun scrollForwardBy(y: Int): Position3D = innerArea.scrollForwardBy(y)
    override fun scrollBackwardBy(y: Int): Position3D = innerArea.scrollBackwardBy(y)
    override fun scrollTo(position3D: Position3D) = innerArea.scrollTo(position3D)

    override fun addEntity(entity: GameEntity, position: Position3D) = innerArea.addEntity(entity, position)
    override fun removeEntity(entity: GameEntity) = innerArea.removeEntity(entity)
}