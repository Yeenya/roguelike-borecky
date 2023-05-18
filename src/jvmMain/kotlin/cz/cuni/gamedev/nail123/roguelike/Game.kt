package cz.cuni.gamedev.nail123.roguelike

import cz.cuni.gamedev.nail123.roguelike.actions.*
import cz.cuni.gamedev.nail123.roguelike.events.GameStep
import cz.cuni.gamedev.nail123.roguelike.world.Direction
import cz.cuni.gamedev.nail123.roguelike.world.World

/**
 * A class containing a state of the game (World) and the game logic.
 */
class Game(val world: World = GameConfig.defaultWorld()) {
    val area
        get() = world.currentArea
    val player
        get() = area.player

    var steps = 0

    /** The possible actions the player may perform. */
    enum class BasicActions(val action: GameAction) {
        MOVE_NORTH(Move(Direction.NORTH)),
        MOVE_EAST(Move(Direction.EAST)),
        MOVE_WEST(Move(Direction.WEST)),
        MOVE_SOUTH(Move(Direction.SOUTH)),
        MOVE_NORTHEAST(Move(Direction.NORTH_EAST)),
        MOVE_SOUTHEAST(Move(Direction.SOUTH_EAST)),
        MOVE_SOUTHWEST(Move(Direction.SOUTH_WEST)),
        MOVE_NORTHWEST(Move(Direction.NORTH_WEST)),
        PICK_UP(PickUp())
    }

    fun getValidActions(): List<GameAction> {
        val list = ArrayList(BasicActions.values().map { it.action })
        for (i in player.inventory.items.indices) {
            list.add(Drop(i))
            list.add(ToggleEquip(i))
        }

        return list
    }

    // The main game loop
    fun step(action: GameAction) {
        if (!isValid(action)) {
            println("Action passed not one of Game's PlayerActions - possible cheating")
            return
        }
        val actionPerformed = action.tryPerform(area)
        if (!actionPerformed) return
        for (entity in area.entities) entity.update()
        ++steps

        GameStep(this).emit()
    }

    fun isValid(action: GameAction) = getValidActions().any { it == action }
}