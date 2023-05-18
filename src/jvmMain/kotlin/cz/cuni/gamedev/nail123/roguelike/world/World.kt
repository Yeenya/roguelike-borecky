package cz.cuni.gamedev.nail123.roguelike.world

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.utils.collections.ObservableList
import cz.cuni.gamedev.nail123.utils.collections.withAddListener
import org.hexworks.zircon.api.builder.component.GameComponentBuilder
import org.hexworks.zircon.api.data.Tile

/**
 * Represents the whole world of the game, in other words, the game state. Implements a linear progression of levels.
 */
abstract class World {
    val areas = ObservableList<Area>().withAddListener { area -> area.world = this }
    private val areaSwitcher = AreaSwitcher(GameConfig.VISIBLE_SIZE, GameConfig.AREA_SIZE)

    init {
        @Suppress("LeakingThis")
        areas.add(buildStartingArea())
        goToArea(areas[0])
    }
    val currentArea
        get() = areaSwitcher.innerArea

    val player: Player
        get() = currentArea.player

    fun goToArea(area: Area) {
        areaSwitcher.switchTo(area)
    }

    abstract fun buildStartingArea(): Area
    abstract fun moveDown()
    abstract fun moveUp()

    companion object {
        fun GameComponentBuilder<Tile, GameBlock>.withWorld(world: World) = this.withGameArea(world.areaSwitcher)
    }
}