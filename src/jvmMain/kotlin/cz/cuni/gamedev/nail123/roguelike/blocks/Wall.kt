package cz.cuni.gamedev.nail123.roguelike.blocks

import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import org.hexworks.zircon.api.data.Tile

class Wall: GameBlock(GameTiles.WALL) {
    override val blocksMovement = true
    override val blocksVision = true

    override var baseTile: Tile by GameTiles.wallTiling
}