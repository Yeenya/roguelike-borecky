package cz.cuni.gamedev.nail123.roguelike.actions

import cz.cuni.gamedev.nail123.roguelike.world.Direction
import cz.cuni.gamedev.nail123.roguelike.world.Area

data class Move(val direction: Direction): GameAction() {
    override fun tryPerform(area: Area): Boolean {
        return area.player.move(direction).isSuccess
    }
}