package cz.cuni.gamedev.nail123.roguelike.entities.attributes

import cz.cuni.gamedev.nail123.roguelike.entities.GameEntity

interface HasCombatStats {
    val maxHitpoints: Int
    var hitpoints: Int
    var attack: Int
    var defense: Int

    fun takeDamage(amount: Int) {
        hitpoints -= amount
        if (hitpoints <= 0) {
            hitpoints = 0
            die()
        }
    }
    fun die() {
        (this as GameEntity?)?.area?.removeEntity(this)
    }
}