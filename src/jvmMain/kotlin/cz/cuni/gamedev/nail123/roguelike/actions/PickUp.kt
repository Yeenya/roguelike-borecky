package cz.cuni.gamedev.nail123.roguelike.actions

import cz.cuni.gamedev.nail123.roguelike.entities.items.Item
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.world.Area

class PickUp: GameAction() {
    override fun tryPerform(area: Area): Boolean {
        val playerPos = area.player.position
        val item = area[playerPos]!!.entities.filterIsInstance<Item>().firstOrNull()
        if (item == null) {
            logMessage("There is nothing to pick up here.")
            return false
        }

        // We directly try to equip - we don't need to deal with inventory management immediately
        return if (item.isEquipable(area.player).success) {
            area.player.tryEquip(item).success
        } else {
            area.player.tryPickUp(item)
            true
        }
    }
}