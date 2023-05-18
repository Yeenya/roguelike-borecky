package cz.cuni.gamedev.nail123.roguelike.world.builders.automata

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.blocks.Floor
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.blocks.Wall
import cz.cuni.gamedev.nail123.roguelike.extensions.floorNeighbors8
import cz.cuni.gamedev.nail123.roguelike.world.builders.AreaBuilder
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

class CellularAutomataAreaBuilder(size: Size3D, visibleSize: Size3D = GameConfig.VISIBLE_SIZE)
    : AreaBuilder(size, visibleSize) {

    override fun create(): AreaBuilder = apply {
        randomizeTiles()
        smoothen(8)
        addPlayer()
    }

    fun addPlayer() = apply {
        addAtEmptyPosition(
                player,
                Position3D.create(0, 0, 0),
                GameConfig.VISIBLE_SIZE
        )
    }

    private fun randomizeTiles() = apply {
        allPositions.forEach { pos ->
            blocks[pos] = if (Math.random() < 0.5) Floor() else Wall()
        }
    }

    protected fun smoothen(iterations: Int) = apply {
        val newBlocks = mutableMapOf<Position3D, GameBlock>()

        repeat(iterations) {
            allPositions.forEach { pos ->
                val neighbors = pos.floorNeighbors8() + pos
                val walls = neighbors.filter { blocks[it] is Wall }.count()
                val floors = neighbors.count() - walls

                newBlocks[pos] = if (floors > walls) Floor() else Wall()
            }
            blocks.putAll(newBlocks)
        }
    }
}