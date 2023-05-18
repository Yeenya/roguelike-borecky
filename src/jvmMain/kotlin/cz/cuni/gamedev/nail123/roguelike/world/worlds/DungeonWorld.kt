package cz.cuni.gamedev.nail123.roguelike.world.worlds

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Stairs
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.mechanics.Pathfinding
import cz.cuni.gamedev.nail123.roguelike.world.Area
import cz.cuni.gamedev.nail123.roguelike.world.World
import cz.cuni.gamedev.nail123.roguelike.world.builders.automata.CellularAutomataAreaBuilder

/**
 * This world provides infinite-depth dungeon delving.
 */
open class DungeonWorld: World() {
    var currentLevel: Int = 0
    val levels
        get() = areas

    override fun buildStartingArea() = buildLevel(0)

    open fun buildLevel(floor: Int): Area {
        val area = CellularAutomataAreaBuilder(GameConfig.AREA_SIZE).create()

        // Add stairs up
        if (floor > 0) area.addEntity(Stairs(false), area.player.position)

        // Add stairs down
        val floodFill = Pathfinding.floodFill(area.player.position, area)
        val staircasePosition = floodFill.keys.random()
        area.addEntity(Stairs(), staircasePosition)

        return area.build()
    }

    override fun moveDown() {
        ++currentLevel
        this.logMessage("Descended to level ${currentLevel + 1}")
        if (currentLevel >= areas.size) levels.add(buildLevel(levels.size))
        goToArea(levels[currentLevel])
    }

    override fun moveUp() {
        --currentLevel
        this.logMessage("Ascended to level ${currentLevel + 1}")
        println("Going up to level $currentLevel")
        goToArea(levels[currentLevel])
    }
}