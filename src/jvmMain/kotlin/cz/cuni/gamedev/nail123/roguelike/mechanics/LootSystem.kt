package cz.cuni.gamedev.nail123.roguelike.mechanics

import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Enemy
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Orc
import cz.cuni.gamedev.nail123.roguelike.entities.enemies.Rat
import cz.cuni.gamedev.nail123.roguelike.entities.items.Item
import cz.cuni.gamedev.nail123.roguelike.entities.items.Sword
import cz.cuni.gamedev.nail123.roguelike.entities.items.Potion
import kotlin.random.Random

object LootSystem {
    interface ItemDrop {
        fun getDrops(): List<Item>
    }
    object NoDrop: ItemDrop {
        override fun getDrops() = listOf<Item>()
    }
    class SingleDrop(val instantiator: () -> Item): ItemDrop {
        override fun getDrops() = listOf(instantiator())
    }
    class TreasureClass(val numDrops: Int, val possibleDrops: List<Pair<Int, ItemDrop>>): ItemDrop {
        val totalProb = possibleDrops.map { it.first }.sum()

        override fun getDrops(): List<Item> {
            val drops = ArrayList<Item>()
            repeat(numDrops) {
                drops.addAll(pickDrop().getDrops())
            }
            return drops
        }

        private fun pickDrop(): ItemDrop {
            var randNumber = Random.Default.nextInt(totalProb)
            for (drop in possibleDrops) {
                randNumber -= drop.first
                if (randNumber < 0) return drop.second
            }
            // Never happens, but we need to place something here anyway
            return possibleDrops.last().second
        }
    }
    // Store rng for convenience
    val rng = Random.Default

    // Sword with power 2-4
    val basicSword = SingleDrop { Sword(rng.nextInt(3) + 2) }
    // Sword with power 5-6
    val rareSword = SingleDrop { Sword(rng.nextInt(2) + 5) }

    val basicPotion = SingleDrop { Potion(rng.nextInt(1, 3))}
    val rarePotion = SingleDrop { Potion(rng.nextInt(5, 10))}

    val enemyDrops = mapOf(
        Rat::class to TreasureClass(1, listOf(
            3 to NoDrop,
            1 to basicSword,
            1 to basicPotion
        )),
        Orc::class to TreasureClass(1, listOf(
            4 to NoDrop,
            2 to basicSword,
            1 to rareSword,
            2 to basicPotion,
            1 to rarePotion
        ))
    )

    fun onDeath(enemy: Enemy) {
        val drops = enemyDrops[enemy::class]?.getDrops() ?: return
        for (item in drops) {
            enemy.area[enemy.position]?.entities?.add(item)
        }
    }

}
