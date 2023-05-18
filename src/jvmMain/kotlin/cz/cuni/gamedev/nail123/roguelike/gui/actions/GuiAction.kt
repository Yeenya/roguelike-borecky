package cz.cuni.gamedev.nail123.roguelike.gui.actions

import cz.cuni.gamedev.nail123.roguelike.gui.IAction
import cz.cuni.gamedev.nail123.roguelike.gui.views.PlayView

abstract class GuiAction: IAction<PlayView> {
    abstract override fun tryPerform(playView: PlayView): Boolean
}