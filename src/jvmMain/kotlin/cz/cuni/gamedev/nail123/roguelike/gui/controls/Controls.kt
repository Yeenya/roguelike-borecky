package cz.cuni.gamedev.nail123.roguelike.gui.controls

import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.api.uievent.UIEventResponse

abstract class Controls {
    abstract fun handleInput(input: UIEvent): UIEventResponse
}