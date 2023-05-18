package cz.cuni.gamedev.nail123.roguelike.world.builders.wavefunctioncollapse

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.blocks.Floor
import cz.cuni.gamedev.nail123.roguelike.blocks.Wall
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Door
import cz.cuni.gamedev.nail123.roguelike.extensions.toIntArray2D
import cz.cuni.gamedev.nail123.roguelike.world.builders.AreaBuilder
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import org.mifek.wfc.datastructures.IntArray2D
import org.mifek.wfc.models.OverlappingCartesian2DModel
import org.mifek.wfc.models.options.Cartesian2DModelOptions
import java.io.File

class WFCAreaBuilder(size: Size3D, visibleSize: Size3D = GameConfig.VISIBLE_SIZE) : AreaBuilder(size, visibleSize) {
    val sourceArray2D = File("src/jvmMain/resources/levels/wfc_sample.txt")
        .readLines()
        .toIntArray2D()

    fun charToBlock(char: Char) = when(char) {
        '#' -> Wall()
        'D' -> Floor().apply { entities.add(Door()) }
        else -> Floor()
    }

    override fun create() = apply {
        val model = OverlappingCartesian2DModel(
            sourceArray2D,
            2,
            width,
            height,
            Cartesian2DModelOptions(
                periodicInput = true
            )
        )

        val output = getOutputFrom(model)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val char = output[x, y].toChar()
                blocks[Position3D.create(x, y, 0)] = charToBlock(char)
            }
        }
    }

    fun getOutputFrom(model: OverlappingCartesian2DModel): IntArray2D {
        val maxAttempts = 10
        val algorithm = model.build()

        for (i in 1..maxAttempts) {
            val success = algorithm.run()
            if (success) break
            if (i == maxAttempts) throw Exception("Wave Function Collapse algorithm didn't find solution!")
        }

        return model.constructOutput(algorithm)
    }
}