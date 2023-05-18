package cz.cuni.gamedev.nail123.roguelike.entities.unplacable

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.mechanics.visiblePositions
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class FogOfWar: GameEntity() {
    override val blocksMovement = false
    override val blocksVision = false

    override fun init() {
        for (block in area.blocks.values) {
            block.top = GameTiles.BLACK
        }
        update()
    }

    override fun update() {
        area.player.visiblePositions().forEach {
            area[it]?.top = GameTiles.EMPTY
        }
    }
}