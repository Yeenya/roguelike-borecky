package cz.cuni.gamedev.nail123.roguelike.gui.controls

import cz.cuni.gamedev.nail123.roguelike.Game
import cz.cuni.gamedev.nail123.roguelike.gui.IAction
import cz.cuni.gamedev.nail123.roguelike.gui.actions.ShowInventory
import org.hexworks.zircon.api.uievent.KeyCode

class KeyboardConfig(val mapping: MutableMap<KeyCode, IAction<*>>) {
    companion object {
        val Default = KeyboardConfig(mutableMapOf(
            // Default 8-directional movement (WASD + QEZC)
            KeyCode.KEY_W to Game.BasicActions.MOVE_NORTH.action,
            KeyCode.KEY_A to Game.BasicActions.MOVE_WEST.action,
            KeyCode.KEY_S to Game.BasicActions.MOVE_SOUTH.action,
            KeyCode.KEY_D to Game.BasicActions.MOVE_EAST.action,
            KeyCode.KEY_E to Game.BasicActions.MOVE_NORTHEAST.action,
            KeyCode.KEY_C to Game.BasicActions.MOVE_SOUTHEAST.action,
            KeyCode.KEY_Z to Game.BasicActions.MOVE_SOUTHWEST.action,
            // Supporting czech keyboard layout
            KeyCode.KEY_Y to Game.BasicActions.MOVE_SOUTHWEST.action,
            KeyCode.KEY_Q to Game.BasicActions.MOVE_NORTHWEST.action,

            // 4-directional movement also on arrow keys
            KeyCode.UP to Game.BasicActions.MOVE_NORTH.action,
            KeyCode.LEFT to Game.BasicActions.MOVE_WEST.action,
            KeyCode.DOWN to Game.BasicActions.MOVE_SOUTH.action,
            KeyCode.RIGHT to Game.BasicActions.MOVE_EAST.action,

            // Other actions
            KeyCode.KEY_P to Game.BasicActions.PICK_UP.action,
            KeyCode.KEY_I to ShowInventory()
        ))
    }
}