package cz.cuni.gamedev.nail123.roguelike.entities.attributes

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.items.Item
import cz.cuni.gamedev.nail123.roguelike.events.InventoryUpdated
import cz.cuni.gamedev.nail123.roguelike.events.logMessage

interface HasInventory {
    val inventory: Inventory

    val items: List<Item>
        get() = inventory.items

    fun tryEquip(item: Item) = inventory.tryEquip(item)
    fun unequip(item: Item) = inventory.unequip(item)
    fun tryPickUp(item: Item) = inventory.add(item)
    fun removeItem(item: Item) = inventory.remove(item)
    fun removeItemAt(index: Int) = inventory.removeAt(index)
}

class Inventory(val player: Player) {
    data class EquipResult(val success: Boolean, val errorMessage: String) {
        companion object {
            val Success = EquipResult(true, "")
        }
    }
    private val _items = ArrayList<Item>()
    val items: List<Item>
        get() = _items

    val equipped: List<Item>
        get() = items.filter { it.isEquipped }
    val unequipped: List<Item>
        get() = items.filter { !it.isEquipped }

    fun tryEquip(item: Item): EquipResult {
        if (item.isEquipped) return EquipResult(false, "Item already equipped")

        val test = item.isEquipable(player)
        if (test.success) {
            add(item)
            item.onEquip(this, player)
            logMessage("Equipped $item")
        } else {
            logMessage(test.errorMessage)
        }
        return test
    }

    fun unequip(item: Item) {
        item.onUnequip(this, player)
    }

    fun add(item: Item) {
        item.block.entities.remove(item)
        if (item !in _items) {
            _items.add(item)
        }
        InventoryUpdated(this).emit()
    }

    fun remove(item: Item) = removeAt(_items.indexOf(item))

    fun removeAt(slot: Int): Item {
        val item = _items[slot]
        _items.removeAt(slot)
        item.onUnequip(this, player)
        return item
    }
}