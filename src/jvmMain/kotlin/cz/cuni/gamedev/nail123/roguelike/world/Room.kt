package cz.cuni.gamedev.nail123.roguelike.world
import kotlin.math.sqrt

data class Room(val x: Int, val y: Int, val width: Int, val height: Int) {
    val connectedWith = mutableListOf<Room>()
    val sortedRooms = mutableListOf<Room>()

    fun getSortedRooms(rooms: MutableList<Room>): MutableList<Room> {
        sortedRooms.addAll(rooms)
        sortedRooms.sortBy {room -> calculateDistance(this, room)}
        sortedRooms.removeAt(0) //remove this room itself
        return sortedRooms
    }

    fun calculateDistance(roomA: Room, roomB: Room): Double {
        val vector = listOf(roomA.x - roomB.x, roomA.y - roomB.y)
        return sqrt((vector[0] * vector[0] + vector[1] * vector[1]).toDouble())
    }

    fun getClosestUnconnectedRoom(): Room {
        var minDistance = Double.MAX_VALUE
        var closestRoom = this
        for (room in sortedRooms) {
            if (room != this && !sortedRooms.contains(room)) {
                val vector = listOf(room.x - x, room.y - y)
                val distance = sqrt((vector[0] * vector[0] + vector[1] * vector[1]).toDouble())
                if (distance < minDistance) {
                    minDistance = distance
                    closestRoom = room
                }
            }
        }
        return closestRoom
    }
}




