package cz.cuni.gamedev.nail123.roguelike

import cz.cuni.gamedev.nail123.roguelike.blocks.Wall
import cz.cuni.gamedev.nail123.roguelike.extensions.asNullable
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import cz.cuni.gamedev.nail123.roguelike.world.Area
import org.hexworks.zircon.internal.tileset.SwingTilesetLoader
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val game = Game()
    game.world.currentArea.toPNG("out/render.png")
}

fun Area.toPNG(filepath: String) {
    val image = BufferedImage(
            width * GameTiles.defaultCharTileset.width,
            height * GameTiles.defaultCharTileset.height,
            BufferedImage.TRANSLUCENT
    )
    val graphics = image.createGraphics()

    val loader = SwingTilesetLoader()

    val charTileset = loader.loadTilesetFrom(GameTiles.defaultCharTileset)

    allPositions.forEach { position ->
        val block = get(position) ?: Wall()

        arrayOf(block.bottom, block.content).forEach {
            val tileset = it.asGraphicTile().asNullable?.let { loader.loadTilesetFrom(it.tileset) } ?: charTileset
            tileset.drawTile(it, graphics, position.to2DPosition())
        }
    }

    val file = File(filepath)
    file.mkdirs()
    ImageIO.write(image, "png", file)
}