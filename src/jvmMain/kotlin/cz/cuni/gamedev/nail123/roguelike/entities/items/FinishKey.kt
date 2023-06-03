package cz.cuni.gamedev.nail123.roguelike.entities.items

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasInventory
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory
import cz.cuni.gamedev.nail123.roguelike.entities.objects.Portal
import cz.cuni.gamedev.nail123.roguelike.tiles.GameTiles
import org.hexworks.zircon.api.data.Position3D

class FinishKey(): Item(GameTiles.FINISHKEY) {
    override fun isEquipable(character: HasInventory): Inventory.EquipResult {
        return Inventory.EquipResult.Success
    }

    override fun onEquip(character: HasInventory) {
        if (character is Player) {
            val position = Position3D.create(character.position.x, character.position.y + 1, character.position.z)
            area[position]?.entities?.add(Portal(false, false))
        }
    }

    override fun onUnequip(character: HasInventory) {
    }

    override fun toString(): String {
        return "Portal Key"
    }
}