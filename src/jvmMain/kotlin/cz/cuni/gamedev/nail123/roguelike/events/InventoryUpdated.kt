package cz.cuni.gamedev.nail123.roguelike.events

import cz.cuni.gamedev.nail123.roguelike.entities.attributes.Inventory

class InventoryUpdated(override val emitter: Inventory): GameEvent() {}