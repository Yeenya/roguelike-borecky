package cz.cuni.gamedev.nail123.roguelike.gui.views

import cz.cuni.gamedev.nail123.roguelike.Game
import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.gui.controls.KeyboardControls
import cz.cuni.gamedev.nail123.roguelike.events.*
import cz.cuni.gamedev.nail123.roguelike.gui.CameraMover
import cz.cuni.gamedev.nail123.roguelike.gui.fragments.InventoryFragment
import cz.cuni.gamedev.nail123.roguelike.gui.fragments.StatsFragment
import cz.cuni.gamedev.nail123.roguelike.world.World.Companion.withWorld
import org.hexworks.cobalt.events.api.DisposeSubscription
import org.hexworks.cobalt.events.api.KeepSubscription
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.view.base.BaseView
import org.hexworks.zircon.internal.Zircon

class PlayView(val tileGrid: TileGrid, val game: Game = Game()): BaseView(tileGrid, ColorThemes.arc()) {
    val keyboardControls = KeyboardControls(game, this)
    val statsFragment = createStatsFragment()
    val inventoryFragment = createInventoryFragment()

    override fun onDock() {
        val sidebar = Components.panel()
                .withSize(GameConfig.SIDEBAR_WIDTH, GameConfig.WINDOW_HEIGHT)
                .withDecorations(
                        ComponentDecorations.box()
                )
                .build()

        sidebar.addFragment(statsFragment)
        sidebar.addFragment(inventoryFragment)

        val logArea = Components.logArea()
                .withSize(GameConfig.WINDOW_WIDTH - GameConfig.SIDEBAR_WIDTH, GameConfig.LOG_AREA_HEIGHT)
                .withAlignmentWithin(screen, ComponentAlignment.BOTTOM_RIGHT)
                .withDecorations(
                        ComponentDecorations.box(title = "Log")
                )
                .build()

        val gameComponent = GameComponents.newGameComponentBuilder<Tile, GameBlock>()
                .withWorld(game.world)
                .withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT)
                .build()

        screen.addComponent(sidebar)
        screen.addComponent(logArea)
        screen.addComponent(gameComponent)

        // Handling key presses
        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, phase ->
            keyboardControls.handleInput(event)
        }

        // Scrolling
        val cameraMover = CameraMover(game.world)
        cameraMover.update()
        game.world.player.positionProperty.onChange {
            cameraMover.update()
        }

        // Logging to log area
        Zircon.eventBus.subscribeTo<LoggedEvent>(key="LoggedEvent") { event ->
            logArea.addParagraph(event.logMessage, withNewLine = false)
            KeepSubscription
        }

        // GameOver
        Zircon.eventBus.subscribeTo<GameOver>(key="GameOver") { event ->
            replaceWith(LoseView(tileGrid))
            DisposeSubscription
        }
    }

    fun createStatsFragment(): StatsFragment {
        val statsFragment = StatsFragment(game.world)

        Zircon.eventBus.subscribeTo<GameStep>(key="GameStep") {
            statsFragment.update()
            KeepSubscription
        }
        statsFragment.update()
        return statsFragment
    }
    fun createInventoryFragment(): InventoryFragment {
        val inventoryFragment = InventoryFragment(game.world)
        Zircon.eventBus.subscribeTo<GameStep>(key="GameStep") {
            inventoryFragment.update()
            KeepSubscription
        }
        Zircon.eventBus.subscribeTo<InventoryUpdated>(key="InventoryUpdated") {
            inventoryFragment.update()
            KeepSubscription
        }
        inventoryFragment.update()
        return inventoryFragment
    }
}