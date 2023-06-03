package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.InteractionType
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.interactionContext
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import org.hexworks.zircon.api.data.Tile

abstract class Consumable(tile: Tile): Item(tile) {
    override fun isEquipable(character: HasInventory): Inventory.EquipResult {
        return Inventory.EquipResult.Success
    }
}