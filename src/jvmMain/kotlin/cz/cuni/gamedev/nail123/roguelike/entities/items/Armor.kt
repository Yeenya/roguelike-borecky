package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class Armor(val value: Int): Item(GameTiles.ARMOR) {
    override fun isEquipable(character: HasInventory): Inventory.EquipResult {
        return if (character.inventory.equipped.filterIsInstance<Armor>().isNotEmpty()) {
            Inventory.EquipResult(false, "Cannot equip two pieces of armor")
        } else Inventory.EquipResult.Success
    }

    override fun onEquip(character: HasInventory) {
        if (character is Player) {
                character.defense += value
        }
    }

    override fun onUnequip(character: HasInventory) {
        if (character is Player) {
            character.defense -= value
        }
    }

    override fun toString(): String {
        return "Armor($value)"
    }
}