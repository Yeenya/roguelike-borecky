package cz.cuni.gamedev.nail123.roguelike

import ch.qos.logback.classic.Level
import cz.cuni.gamedev.nail123.roguelike.gui.views.StartView
import org.hexworks.zircon.api.SwingApplications
import org.slf4j.LoggerFactory


fun main() {
    turnOffExcessiveLogging()

    val tileGrid = SwingApplications.startTileGrid(GameConfig.buildAppConfig())
    StartView(tileGrid).dock()
}


fun turnOffExcessiveLogging() {
    arrayOf(
            "org.hexworks.zircon.internal.application.SwingApplication",
            "org.hexworks.cobalt.events.internal.DefaultEventBus",
            "org.hexworks.cobalt.databinding.internal.property.DefaultProperty",
            "org.hexworks.zircon.internal.uievent.impl.DefaultUIEventProcessor",
            "org.hexworks.zircon.internal.uievent.impl.UIEventToComponentDispatcher",
            "org.hexworks.zircon.internal.renderer.SwingCanvasRenderer\$mouseEventListener\$1",
            "org.hexworks.zircon.internal.uievent.UIEventDispatcher",
            "org.hexworks.zircon.api.grid.TileGrid",
            "org.hexworks.zircon.internal.component.impl.DefaultButton",
            "org.hexworks.zircon.internal.game.impl.DefaultGameComponent",
            "org.hexworks.zircon.internal.behavior.ComponentFocusOrderList",
            "org.hexworks.cobalt.databinding.internal.collections.DefaultListProperty"
    ).forEach {
        (LoggerFactory.getLogger(it) as ch.qos.logback.classic.Logger).level = Level.WARN
    }
}