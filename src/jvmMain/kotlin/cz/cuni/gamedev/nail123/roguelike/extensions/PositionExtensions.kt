package cz.cuni.gamedev.nail123.roguelike.extensions

import cz.cuni.gamedev.nail123.roguelike.world.Direction
import org.hexworks.zircon.api.data.Position3D
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.max

fun Position3D.floorNeighbors8() = listOf(
        Position3D.create(x + 1, y, 0),
        Position3D.create(x, y + 1, 0),
        Position3D.create(x - 1, y, 0),
        Position3D.create(x, y - 1, 0),
        Position3D.create(x + 1, y + 1, 0),
        Position3D.create(x - 1, y + 1, 0),
        Position3D.create(x + 1, y - 1, 0),
        Position3D.create(x - 1, y - 1, 0)
)

fun Position3D.floorNeighbors4() = listOf(
        Position3D.create(x + 1, y, 0),
        Position3D.create(x, y + 1, 0),
        Position3D.create(x - 1, y, 0),
        Position3D.create(x, y - 1, 0)
)

// I would've made this the plus operator, but IDEA doesn't autocomplete it, since there already is a plus method
// of adding two positions together
/**
 * Returns a new position shifted by the given direction.
 */
infix fun Position3D.shift(direction: Direction) = when(direction) {
    Direction.NORTH -> withRelativeY(-1)
    Direction.NORTH_EAST -> withRelativeY(-1).withRelativeX(1)
    Direction.EAST -> withRelativeX(1)
    Direction.SOUTH_EAST -> withRelativeX(1).withRelativeY(1)
    Direction.SOUTH -> withRelativeY(1)
    Direction.SOUTH_WEST -> withRelativeY(1).withRelativeX(-1)
    Direction.WEST -> withRelativeX(-1)
    Direction.NORTH_WEST -> withRelativeX(-1).withRelativeY(-1)
    Direction.UP -> withRelativeZ(-1)
    Direction.DOWN -> withRelativeZ(-1)
}

val Position3D.manhattanDistance: Int
    get() = abs(x) + abs(y) + abs(z)

val Position3D.euclideanDistance: Double
    get() = sqrt((x * x + y * y + z * z).toDouble())

val Position3D.chebyshevDistance: Int
    get() = max(max(abs(x), abs(y)), abs(z))