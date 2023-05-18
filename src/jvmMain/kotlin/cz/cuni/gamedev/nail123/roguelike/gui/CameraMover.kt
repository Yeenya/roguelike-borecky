package cz.cuni.gamedev.nail123.roguelike.gui

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.world.World
import org.hexworks.zircon.api.data.Position3D
import kotlin.math.max

class CameraMover(val world: World) {
    fun update() {
        val player = world.player

        val leeway = world.currentArea.size - GameConfig.VISIBLE_SIZE
        val maxScrollX = max(leeway.xLength, 0)
        val maxScrollY = max(leeway.yLength, 0)

        Position3D.create(
                (player.x - GameConfig.VISIBLE_SIZE.xLength / 2).coerceIn(0, maxScrollX),
                (player.y - GameConfig.VISIBLE_SIZE.yLength / 2).coerceIn(0, maxScrollY),
                player.z
        ).let {
            world.currentArea.scrollTo(it)
        }
    }
}