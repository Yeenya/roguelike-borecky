package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.MovingEntity
import cz.cuni.gamedev.nail123.roguelike.world.Area
import cz.cuni.gamedev.nail123.roguelike.world.Direction
import cz.cuni.gamedev.nail123.roguelike.world.IArea
import org.hexworks.zircon.api.data.Position3D
import kotlin.math.abs

object Navigation {
    fun bestDirection(from: Position3D, to: Position3D): Direction? {
        val diff = (to - from)
        val coercedDiff = Position3D.create(
                diff.x.coerceIn(-1, 1),
                diff.y.coerceIn(-1, 1),
                0
        )
        return Direction.fromPosition(coercedDiff)
    }

    fun pathTo(from: Position3D, to: Position3D, area: Area): Direction? {
        val positionsList = Pathfinding.aStar(from, to, area)
        if (positionsList == null || positionsList.path.isEmpty()) return null

        val nextPosition = positionsList.path.first()
        return Direction.fromPosition(nextPosition - from)
    }
}

fun MovingEntity.goBlindlyTowards(target: Position3D) {
    val dir = Navigation.bestDirection(position, target)
    if (dir != null) move(dir)
}

fun MovingEntity.goSmartlyTowards(target: Position3D) {
    val dir = Navigation.pathTo(position, target, area)
    if (dir != null) move(dir)
}