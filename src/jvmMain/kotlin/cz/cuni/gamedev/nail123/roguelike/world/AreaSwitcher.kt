package cz.cuni.gamedev.nail123.roguelike.world

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.world.builders.EmptyAreaBuilder
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.base.BaseGameArea

/**
 * This class looks like a BaseGameArea on the outside, but it is actually just a wrapper around a (switchable) Area.
 * It is an adapter for:
 *  - drawing - you can transition from one area to another within the same component
 *  - world - you can (if you want) only update entities in the current area
 */
class AreaSwitcher(visibleSize: Size3D, actualSize: Size3D): AreaDecorator(
        visibleSize, actualSize
    ), IArea {

    override var innerArea = EmptyAreaBuilder().build()
        private set

    val player
        get() = innerArea.player

    fun switchTo(area: Area) {
        val player = innerArea.player

        innerArea = area
        state = state.copy(blocks = area.state.blocks)

        // We are keeping the player object in between areas
        if (player.position != Position3D.unknown()) {
            removeEntity(innerArea.player)
            addEntity(player, innerArea.player.position)
            innerArea.player = player
        }
    }
}