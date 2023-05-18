package cz.cuni.gamedev.nail123.roguelike.actions

import cz.cuni.gamedev.nail123.roguelike.events.InventoryUpdated
import cz.cuni.gamedev.nail123.roguelike.world.Area
import io.github.gabrielshanahan.moroccode.compareUsingFields
import io.github.gabrielshanahan.moroccode.hash

class ToggleEquip(val slot: Int): GameAction() {
    override fun tryPerform(area: Area): Boolean {
        val player = area.player
        if (slot !in player.items.indices) {
            println("Trying to equip from outside the range")
            return false
        }
        val item = player.items[slot]
        if (item.isEquipped) {
            player.unequip(item)
            return true
        }
        return area.player.tryEquip(item).success
    }

    override fun equals(other: Any?) = compareUsingFields(other) { one.slot == two.slot }
    override fun hashCode() = hash(javaClass, slot)
}