package cz.cuni.gamedev.nail123.roguelike.gui.actions

import cz.cuni.gamedev.nail123.roguelike.actions.Drop
import cz.cuni.gamedev.nail123.roguelike.actions.ToggleEquip
import cz.cuni.gamedev.nail123.roguelike.events.InventoryUpdated
import cz.cuni.gamedev.nail123.roguelike.gui.controls.KeyboardConfig
import cz.cuni.gamedev.nail123.roguelike.gui.views.PlayView
import org.hexworks.zircon.api.uievent.KeyCode

class ShowInventory: GuiAction() {
    class InventoryContext(val playView: PlayView) {
        class ContextAction(val contextFun: (PlayView) -> Unit): GuiAction() {
            override fun tryPerform(playView: PlayView): Boolean {
                contextFun(playView)
                return true
            }
        }

        val oldConfig = playView.keyboardControls.config
        var cursorPosition = 0
            set(value) {
                field = value
                cursorMoved()
            }
        val inventory = playView.game.area.player.inventory

        val moveUp = ContextAction { cursorPosition = (cursorPosition - 1).coerceAtLeast(0) }
        val moveDown = ContextAction { cursorPosition = (cursorPosition + 1).coerceAtMost(inventory.items.size - 1) }

        val config = KeyboardConfig(mutableMapOf(
            KeyCode.UP to moveUp,
            KeyCode.DOWN to moveDown,
            KeyCode.KEY_W to moveUp,
            KeyCode.KEY_S to moveDown,
            KeyCode.KEY_E to ToggleEquip(0),
            KeyCode.KEY_D to Drop(0),
            KeyCode.KEY_I to ContextAction { deactivate() }
        ))

        fun cursorMoved() {
            playView.keyboardControls.config.mapping[KeyCode.KEY_D] = Drop(cursorPosition)
            playView.keyboardControls.config.mapping[KeyCode.KEY_E] = ToggleEquip(cursorPosition)
            playView.inventoryFragment.selectedIndex = cursorPosition
            InventoryUpdated(inventory).emit()
        }

        fun activate() {
            playView.keyboardControls.config = config
            cursorMoved()
        }

        fun deactivate() {
            cursorPosition = -1
            playView.keyboardControls.config = oldConfig
            InventoryUpdated(inventory).emit()
        }
    }

    override fun tryPerform(playView: PlayView): Boolean {
        val context = InventoryContext(playView)
        context.activate()
        return true
    }
}