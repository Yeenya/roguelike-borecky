package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import cz.cuni.gamedev.nail123.roguelike.entities.items.Axe
import cz.cuni.gamedev.nail123.roguelike.entities.items.Weapon
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import kotlin.math.max
import kotlin.random.Random
import kotlin.reflect.typeOf

object Combat {
    /**
     * Very basic combat dealing damage equal to difference between attacker's attack and defender's defense.
     * Meant to be expanded.
     */
    fun attack(attacker: HasCombatStats, defender: HasCombatStats) {
        var damage = max(attacker.attack - defender.defense, 0)
        if ((attacker is Player)) {
            var critChance = 0f
            for (item in attacker.inventory.equipped) {
                if (item is Weapon) {
                    critChance = item.criticalStrikeChance
                }
            }
            if (Random.nextFloat() <= critChance) {
                damage *= 2
                this.logMessage("Critical hit!")
            }
        }
        defender.takeDamage(damage)

        when {
            attacker is Player -> this.logMessage("You hit $defender for $damage damage!")
            defender is Player -> this.logMessage("$attacker hits you for $damage damage!")
            else -> this.logMessage("$attacker hits $defender for $damage damage!")
        }
    }

}