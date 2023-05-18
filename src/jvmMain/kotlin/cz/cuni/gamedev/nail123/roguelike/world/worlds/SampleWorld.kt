package cz.cuni.gamedev.nail123.roguelike.world.worlds

import cz.cuni.gamedev.nail123.roguelike.blocks.Floor
import cz.cuni.gamedev.nail123.roguelike.blocks.Wall
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Rat
import cz.cuni.gamedev.nail123.roguelike.entities.items.Sword
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Stairs
import cz.cuni.gamedev.nail123.roguelike.entities.unplacable.FogOfWar
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.world.Area
import cz.cuni.gamedev.nail123.roguelike.world.Room
import cz.cuni.gamedev.nail123.roguelike.world.World
import cz.cuni.gamedev.nail123.roguelike.world.builders.EmptyAreaBuilder
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D
import kotlin.random.Random

/**
 * Sample world, made as a starting point for creating custom worlds.
 * It consists of separate levels - each one has one staircase, and it leads infinitely deep.
 */
class SampleWorld: World() {
    var currentLevel = 0

    override fun buildStartingArea() = buildLevel()

    /**
     * Builds one of the levels.
     */
    fun buildLevel(): Area {
        // Start with an empty area and fill it with only walls
        val areaBuilder = EmptyAreaBuilder().create()
        for (x in 0 until areaBuilder.width) {
            for (y in 0 until areaBuilder.height) {
                areaBuilder.blocks[Position3D.create(x, y, 0)] = Wall()
            }
        }

        // Some constants for rooms spawning
        val random = Random(System.currentTimeMillis())
        val rooms = mutableListOf<Room>()
        val count = random.nextInt(5, 10)
        val minRoomSize = 5
        val maxRoomSize = 10

        // Create 'count' rooms with random sizes and positions which don't intersect each other
        while (rooms.size < count) {
            val xSize = random.nextInt(minRoomSize, maxRoomSize)
            val ySize = random.nextInt(minRoomSize, maxRoomSize)
            val x = random.nextInt(areaBuilder.width - xSize - 1) + 1
            val y = random.nextInt(areaBuilder.height - ySize - 1) + 1

            val newRoom = Room(x, y, xSize, ySize)

            if (!rooms.any { isIntersecting(it, newRoom) }) {
                rooms.add(newRoom)
            }
        }

        // "Dig" the area where the rooms are
        for (room in rooms) {
            for (x in room.x until room.x + room.width) {
                for (y in room.y until room.y + room.height) {
                    areaBuilder.blocks[Position3D.create(x, y, 0)] = Floor()
                }
            }
        }

        // Connect each room with its closest neighboring room and connect them via a path
        for (room in rooms) {
            val sortedRooms = room.getSortedRooms(rooms)
            for (sortedRoom in sortedRooms) {
                // Avoid creating two parallel paths
                if (!room.connectedWith.contains(sortedRoom)) {
                    room.connectedWith.add(sortedRoom)
                    sortedRoom.connectedWith.add(room)
                    // Get the path using the buildPathPerpendicular function
                    val path = buildPathPerpendicular(room, sortedRoom)
                    // Build the path
                    for (point in path) {
                        val isBorder =
                            point[0] == 0 || point[0] == areaBuilder.width - 1 || point[1] == 0 || point[1] == areaBuilder.height - 1
                        if (isBorder) break
                        areaBuilder.blocks[Position3D.create(point[0], point[1], 0)] = Floor()
                    }
                    break
                }
            }
        }

        // Check for possible unreachability of some rooms (more than one "graph" of rooms might exist) and fix it
        var roomsMissingConnection = searchConnections(rooms)
        while (roomsMissingConnection.isNotEmpty()) {
            for (room in roomsMissingConnection) {
                // Find the closest unconnected road or (if not found) random unconnected room
                var closestUnconnectedRoom = room.getClosestUnconnectedRoom()
                if (closestUnconnectedRoom == room) {
                    closestUnconnectedRoom = room.connectedWith[0]
                    while (room.connectedWith.contains(closestUnconnectedRoom)) {
                        closestUnconnectedRoom = room.sortedRooms[random.nextInt(room.sortedRooms.size)]
                    }
                }
                // Same as above - connecting rooms via a path
                room.connectedWith.add(closestUnconnectedRoom)
                closestUnconnectedRoom.connectedWith.add(room)
                val path = buildPathPerpendicular(room, closestUnconnectedRoom)
                for (point in path) {
                    val isBorder =
                        point[0] == 0 || point[0] == areaBuilder.width - 1 || point[1] == 0 || point[1] == areaBuilder.height - 1
                    if (isBorder) break
                    areaBuilder.blocks[Position3D.create(point[0], point[1], 0)] = Floor()
                }
            }
            roomsMissingConnection = searchConnections(rooms)
        }

        // Spawn player in a random room
        areaBuilder.addAtEmptyPosition(
                areaBuilder.player,
                Position3D.create(rooms[0].x, rooms[0].y, 0),
                Size3D.create(areaBuilder.width / 2 - 2, areaBuilder.height / 2 - 2, 1)
        )

        // Add stairs to a room which is the most distant from the player room
        areaBuilder.addAtEmptyPosition(
                Stairs(),
                Position3D.create(rooms[0].sortedRooms.last().x, rooms[0].sortedRooms.last().y, 0),
                Size3D.create(areaBuilder.width / 2 - 2, areaBuilder.height / 2 - 2, 1)
        )

        // Add some rats to random rooms each level
        repeat(currentLevel + 1) {
            val randomRoom = rooms.random()
            areaBuilder.addAtEmptyPosition(Rat(), Position3D.create(randomRoom.x, randomRoom.y, 0), areaBuilder.size)
        }

        // Add a sword to every level with increasing attackPower
        val randomRoom = rooms.random()
        areaBuilder.addAtEmptyPosition(Sword(currentLevel * 2), Position3D.create(randomRoom.x, randomRoom.y, 0), areaBuilder.size)

        // We add fog of war such that exploration is needed
        areaBuilder.addEntity(FogOfWar(), Position3D.unknown())

        // Build it into a full Area
        return areaBuilder.build()
    }

    /**
     * Moving down - goes to a brand new level.
     */
    override fun moveDown() {
        ++currentLevel
        this.logMessage("Descended to level ${currentLevel + 1}")
        if (currentLevel >= areas.size) areas.add(buildLevel())
        goToArea(areas[currentLevel])
    }

    /**
     * Moving up would be for revisiting past levels, we do not need that. Check [DungeonWorld] for an implementation.
     */
    override fun moveUp() {
        // Not implemented
    }

    /**
     * Checks if two rooms intersect each other
     */
    fun isIntersecting(roomA: Room, roomB: Room): Boolean {
        return roomA.x < roomB.x + roomB.width &&
                roomA.x + roomA.width > roomB.x &&
                roomA.y < roomB.y + roomB.height &&
                roomA.y + roomA.height > roomB.y
    }

    /**
     * Creates a list of [x,y] points where path connecting two rooms should go. The path is S-shaped
     */
    fun buildPathPerpendicular(roomA: Room, roomB: Room): List<List<Int>> {
        val vectorToRoomB = listOf(roomB.x - roomA.x, roomB.y - roomA.y)
        var startingPointA = listOf(roomA.x, roomA.y)
        var startingPointB = listOf(roomB.x, roomB.y)

        // Decide where to create the starting points from each room, so that they are close to each other
        if (vectorToRoomB[0] >= 0) {
            if (vectorToRoomB[1] >= 0) {
                //right-top quadrant
                startingPointA = randomInRightTopQuadrant(roomA)
                startingPointB = randomInLeftBottomQuadrant(roomB)
            } else {
                //right-bottom quadrant
                startingPointA = randomInRightBottomQuadrant(roomA)
                startingPointB = randomInLeftTopQuadrant(roomB)
            }
        } else {
            if (vectorToRoomB[1] >= 0) {
                //left-top quadrant
                startingPointA = randomInLeftTopQuadrant(roomA)
                startingPointB = randomInRightBottomQuadrant(roomB)
            } else {
                //left-bottom quadrant
                startingPointA = randomInLeftBottomQuadrant(roomA)
                startingPointB = randomInRightTopQuadrant(roomB)
            }
        }

        val path = mutableListOf<List<Int>>()
        val startingPointsVector = listOf(startingPointB[0] - startingPointA[0], startingPointB[1] - startingPointA[1])

        // Check direction where the path should go specifically for the starting points
        if (startingPointsVector[0] >= 0) {
            // xMidpoint is the x coordinate in the middle of the S-shaped path
            val xMidpoint = roomA.x + roomA.width + (roomB.x - (roomA.x + roomA.width)) / 2

            // Add the first and last lines of the S-shape
            for (x in startingPointA[0] until xMidpoint) {
                path.add(listOf(x, startingPointA[1]))
            }
            for (x in xMidpoint until startingPointB[0]) {
                path.add(listOf(x, startingPointB[1]))
            }
            // Add the perpendicular part of the S-shape, either going up or down
            if (startingPointsVector[1] >= 0) {
                //right-top
                for (y in startingPointA[1] until startingPointB[1] + 1) {
                    path.add(listOf(xMidpoint, y))
                }
            } else {
                //right-bottom
                for (y in startingPointB[1] until startingPointA[1] + 1)  {
                    path.add(listOf(xMidpoint, y))
                }
            }
        } else {
            // Same as above, just switched boundaries for the loops if the rooms' x coords are in opposite order
            val xMidpoint = roomB.x + roomB.width + (roomA.x - (roomB.x + roomB.width)) / 2
            for (x in startingPointB[0] until xMidpoint) {
                path.add(listOf(x, startingPointB[1]))
            }
            for (x in xMidpoint until startingPointA[0]) {
                path.add(listOf(x, startingPointA[1]))
            }
            if (startingPointsVector[1] >= 0) {
                //left-top
                for (y in startingPointA[1] until startingPointB[1] + 1) {
                    path.add(listOf(xMidpoint, y))
                }
            } else {
                //left-bottom
                for (y in startingPointB[1] until startingPointA[1] + 1)  {
                    path.add(listOf(xMidpoint, y))
                }
            }
        }
        return path
    }

    /**
     * Create a random [x,y] point at the border of a room's left top quadrant where a path should start
     */
    fun randomInLeftTopQuadrant(room: Room): List<Int> {
        val random = Random(System.currentTimeMillis())
        val x = random.nextInt(room.x, room.x + room.width / 2)
        val y = if (x > room.x) {
            room.y + room.height
        } else {
            random.nextInt(room.y + room.height / 2, room.y + room.height)
        }
        return listOf(x, y)
    }

    /**
     * Create a random [x,y] point at the border of a room's left bottom quadrant where a path should start
     */
    fun randomInLeftBottomQuadrant(room: Room): List<Int> {
        val random = Random(System.currentTimeMillis())
        val x = random.nextInt(room.x, room.x + room.width / 2)
        val y = if (x > room.x) {
            room.y
        } else {
            random.nextInt(room.y, room.y + room.height / 2)
        }
        return listOf(x, y)
    }

    /**
     * Create a random [x,y] point at the border of a room's right top quadrant where a path should start
     */
    fun randomInRightTopQuadrant(room: Room): List<Int> {
        val random = Random(System.currentTimeMillis())
        val x = random.nextInt(room.x + room.width / 2, room.x + room.width)
        val y = if (x < room.x + room.width) {
            room.y + room.height
        } else {
            random.nextInt(room.y + room.height / 2, room.y + room.height)
        }
        return listOf(x, y)
    }

    /**
     * Create a random [x,y] point at the border of a room's right bottom quadrant where a path should start
     */
    fun randomInRightBottomQuadrant(room: Room): List<Int> {
        val random = Random(System.currentTimeMillis())
        val x = random.nextInt(room.x + room.width / 2, room.x + room.width)
        val y = if (x < room.x + room.width) {
            room.y
        } else {
            random.nextInt(room.y, room.y + room.height / 2)
        }
        return listOf(x, y)
    }

    /**
     * Finds all rooms that are not accessible from the first room of the rooms list through simple BFS search
     */
    fun searchConnections(rooms: MutableList<Room>): MutableList<Room> {
        val roomsMissingConnection = mutableListOf<Room>()
        roomsMissingConnection.addAll(rooms)
        val roomsVisited = mutableListOf<Room>()
        roomsVisited.add(rooms[0])
        val roomsToVisit = rooms[0].connectedWith
        // BFS itself
        while (roomsToVisit.isNotEmpty()) {
            val visitingRoom = roomsToVisit.removeAt(0)
            println("Added $visitingRoom to $roomsVisited")
            roomsVisited.add(visitingRoom)
            for (room in visitingRoom.connectedWith) {
                if (!roomsVisited.contains(room)) {
                    roomsToVisit.add(room)
                }
            }
        }
        roomsMissingConnection.removeAll(roomsVisited)
        println("Rooms not connected are $roomsMissingConnection")
        return roomsMissingConnection
    }
}