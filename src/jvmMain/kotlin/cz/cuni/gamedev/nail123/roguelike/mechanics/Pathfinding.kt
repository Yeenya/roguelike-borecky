package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.blocks.Floor
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Door
import cz.cuni.gamedev.nail123.roguelike.extensions.chebyshevDistance
import cz.cuni.gamedev.nail123.roguelike.extensions.floorNeighbors4
import cz.cuni.gamedev.nail123.roguelike.extensions.floorNeighbors8
import cz.cuni.gamedev.nail123.roguelike.extensions.manhattanDistance
import cz.cuni.gamedev.nail123.roguelike.world.IArea
import org.hexworks.zircon.api.data.Position3D
import java.util.ArrayDeque

object Pathfinding {
    data class Result(val path: List<Position3D>, val cost: Int)

    // Some methods of finding neighbors
    val eightDirectional = { pos: Position3D -> pos.floorNeighbors8() }
    val fourDirectional = { pos: Position3D -> pos.floorNeighbors4() }

    // Heuristics
    val manhattan = { pos1: Position3D, pos2: Position3D -> (pos1 - pos2).manhattanDistance }
    val chebyshev = { pos1: Position3D, pos2: Position3D -> (pos1 - pos2).chebyshevDistance }

    // Blocking options
    val defaultBlocking = { block: GameBlock -> block.blocksMovement }
    val doorOpening = { block: GameBlock ->
        (block !is Floor && block.blocksMovement) || block.entities.filter { it !is Door && it.blocksMovement }.any()
    }

    /**
     * Based of https://rosettacode.org/wiki/A*_search_algorithm#Kotlin
     *
     * Implementation of the A* Search Algorithm to find the optimum path between 2 points on a grid.
     *
     * The Grid contains the details of the barriers and methods which supply the neighboring vertices and the
     * cost of movement between 2 cells.  Examples use a standard Grid which allows movement in 8 directions
     * (i.e. includes diagonals) but alternative implementation of Grid can be supplied.
     */
    fun aStar(start: Position3D,
              finish: Position3D,
              area: IArea,
              movement: (Position3D) -> List<Position3D> = eightDirectional,
              blocking: (GameBlock) -> Boolean = defaultBlocking,
              heuristic: (Position3D, Position3D) -> Int = chebyshev,
              moveCost: (Position3D, Position3D) -> Int = { _, _ -> 1 }): Result? {

        /**
         * Use the cameFrom values to Backtrack to the start position to generate the path
         */

        // This should have a better data structure
        val openVertices = mutableSetOf(start)
        val closedVertices = mutableSetOf<Position3D>()
        val costFromStart = mutableMapOf(start to 0)
        val estimatedTotalCost = mutableMapOf(start to heuristic(start, finish))

        val cameFrom = mutableMapOf<Position3D, Position3D>()

        while (openVertices.size > 0) {
            val currentPos = openVertices.minByOrNull { estimatedTotalCost.getValue(it) }!!

            if (currentPos == finish) {
                // We have reached the finish, return the optimal path
                val path = mutableListOf(currentPos)
                var current = currentPos
                while (cameFrom.containsKey(current)) {
                    current = cameFrom.getValue(current)
                    path.add(0, current)
                }
                return Result(path.drop(1).toList(), estimatedTotalCost[finish]!!)
            }

            // We have not reached the finish, mark the current vertex as closed and expand
            openVertices.remove(currentPos)
            closedVertices.add(currentPos)

            movement(currentPos)
                .filter {
                    // Filter blocked path except finish
                    area[it]?.let { block -> !blocking(block) || it == finish } == true
                            && !closedVertices.contains(it)
                }
                .forEach { newPos ->

                val score = costFromStart[currentPos]!! + moveCost(currentPos, newPos)
                if (score < costFromStart.getOrDefault(newPos, Int.MAX_VALUE)) {
                    if (!openVertices.contains(newPos)) openVertices.add(newPos)
                    cameFrom[newPos] = currentPos
                    costFromStart[newPos] = score
                    estimatedTotalCost[newPos] = score + heuristic(newPos, finish)
                }
            }
        }
        return null
    }

    /**
     * A simple breadth-first search to get distances to all positions.
     */
    fun floodFill(start: Position3D,
                  area: IArea,
                  movement: (Position3D) -> List<Position3D> = eightDirectional,
                  blocking: (GameBlock) -> Boolean = doorOpening): Map<Position3D, Int> {

        data class PointWithDistance(val position: Position3D, val distance: Int)

        val allDistances = mutableMapOf<Position3D, Int>()
        val openVertices = ArrayDeque<PointWithDistance>()

        openVertices.add(PointWithDistance(start, 0))
        allDistances[start] = 0

        while (openVertices.isNotEmpty()) {
            val currentPos = openVertices.pollFirst()
            movement(currentPos.position)
                    .filter { area[it]?.let { block -> blocking(block) } == false && !allDistances.containsKey(it) }
                    .forEach {
                        openVertices.addLast(PointWithDistance(it, currentPos.distance + 1))
                        allDistances[it] = currentPos.distance + 1
                    }
        }

        return allDistances
    }
}