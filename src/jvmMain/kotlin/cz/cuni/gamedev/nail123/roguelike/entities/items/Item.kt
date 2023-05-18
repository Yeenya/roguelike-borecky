package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity
import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.*
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import org.hexworks.zircon.api.data.Tile

abstract class Item(tile: Tile): GameEntity(tile), Interactable {
    override val blocksMovement = false
    override val blocksVision = false
    var isEquipped = false
        private set

    abstract fun isEquipable(character: HasInventory): Inventory.EquipResult

    fun onEquip(inventory: Inventory, character: HasInventory) {
        if (isEquipped) return
        isEquipped = true
        onEquip(character)
    }
    fun onUnequip(inventory: Inventory, character: HasInventory) {
        if (!isEquipped) return
        isEquipped = false
        onUnequip(character)
    }

    override fun acceptInteractFrom(other: GameEntity, type: InteractionType) = interactionContext(other, type) {
        withEntity<Player>(type) { logMessage("Here lies ${this@Item}") }
    }

    protected abstract fun onEquip(character: HasInventory)
    protected abstract fun onUnequip(character: HasInventory)
}
