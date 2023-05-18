package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.Player
import cz.cuni.gamedev.nail123.roguelike.entities.attributes.HasCombatStats
import cz.cuni.gamedev.nail123.roguelike.events.logMessage
import kotlin.math.max

object Combat {
    /**
     * Very basic combat dealing damage equal to difference between attacker's attack and defender's defense.
     * Meant to be expanded.
     */
    fun attack(attacker: HasCombatStats, defender: HasCombatStats) {
        val damage = max(attacker.attack - defender.defense, 0)
        defender.takeDamage(damage)

        when {
            attacker is Player -> this.logMessage("You hit $defender for $damage damage!")
            defender is Player -> this.logMessage("$attacker hits you for $damage damage!")
            else -> this.logMessage("$attacker hits $defender for $damage damage!")
        }
    }
}