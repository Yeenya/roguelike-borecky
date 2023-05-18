package cz.cuni.gamedev.nail123.roguelike.world

import org.hexworks.zircon.api.data.Position3D

enum class Direction(val flag: Int) {
    NORTH(1), WEST(2), EAST(4), SOUTH(8), NORTH_WEST(16), NORTH_EAST(32),
    SOUTH_WEST(64), SOUTH_EAST(128), UP(256), DOWN(512);

    val opposite: Direction
        get() = when (this) {
            NORTH -> SOUTH
            NORTH_EAST -> SOUTH_WEST
            EAST -> WEST
            SOUTH_EAST -> NORTH_WEST
            SOUTH -> NORTH
            SOUTH_WEST -> NORTH_EAST
            WEST -> EAST
            NORTH_WEST -> SOUTH_EAST
            UP -> DOWN
            DOWN -> UP
        }

    operator fun plus (other:Direction): Int = this.flag + other.flag

    companion object {
        val eightDirections = arrayOf(NORTH, WEST, EAST, SOUTH, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST)
        val fourDirections = arrayOf(NORTH, WEST, EAST, SOUTH)

        fun fromPosition(pos: Position3D): Direction? {
            return when {
                pos.x == -1 && pos.y == -1 -> NORTH_WEST
                pos.x == 0 && pos.y == -1 -> NORTH
                pos.x == 1 && pos.y == -1 -> NORTH_EAST
                pos.x == -1 && pos.y == 0 -> WEST
                pos.x == 1 && pos.y == 0 -> EAST
                pos.x == -1 && pos.y == 1 -> SOUTH_WEST
                pos.x == 0 && pos.y == 1 -> SOUTH
                pos.x == 1 && pos.y == 1 -> SOUTH_EAST
                else -> null
            }
        }
    }
}
