package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class Axe(val attackPower: Int): Weapon(GameTiles.AXE) {
    override fun onEquip(character: HasInventory) {
        if (character is Player) {
            character.attack += attackPower
        }
    }

    override fun onUnequip(character: HasInventory) {
        if (character is Player) {
            character.attack -= attackPower
        }
    }

    override fun toString(): String {
        return "Axe($attackPower)"
    }

    override val criticalStrikeChance = 0.25f
}