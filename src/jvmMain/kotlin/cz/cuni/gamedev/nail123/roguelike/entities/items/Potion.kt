package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles

class Potion(val healingPower: Int): Consumable(GameTiles.POTION) {
    override fun onEquip(character: HasInventory) {
        if (character is Player) {
            character.hitpoints += healingPower
            if (character.hitpoints > character.maxHitpoints)
                character.hitpoints = character.maxHitpoints
            character.inventory.remove(this)
        }
    }

    override fun onUnequip(character: HasInventory) {
    }

    override fun toString(): String {
        return "Potion($healingPower)"
    }
}