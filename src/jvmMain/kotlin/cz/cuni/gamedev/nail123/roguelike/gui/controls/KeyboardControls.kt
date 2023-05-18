package cz.cuni.gamedev.nail123.roguelike.gui.controls

import cz.cuni.gamedev.nail123.roguelike.Game
import cz.cuni.gamedev.nail123.roguelike.actions.GameAction
import cz.cuni.gamedev.nail123.roguelike.gui.actions.GuiAction
import cz.cuni.gamedev.nail123.roguelike.gui.views.PlayView
import org.hexworks.zircon.api.uievent.KeyboardEvent
import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.api.uievent.UIEventResponse

class KeyboardControls(val game: Game, val playView: PlayView): Controls() {
    var config = KeyboardConfig.Default

    override fun handleInput(input: UIEvent): UIEventResponse {
        // Validate whether the key pressed is valid
        if (input !is KeyboardEvent) return UIEventResponse.pass()
        val action = config.mapping[input.code] ?: return UIEventResponse.pass()

        if (action is GuiAction) {
            action.tryPerform(playView)
        } else if (action is GameAction) {
            // Perform the action
            game.step(action)
        } else {
            return UIEventResponse.pass()
        }
        return UIEventResponse.processed()
    }
}