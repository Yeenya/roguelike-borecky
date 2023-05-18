package cz.cuni.gamedev.nail123.roguelike.gui.fragments

import cz.cuni.gamedev.nail123.roguelike.GameConfig
import cz.cuni.gamedev.nail123.roguelike.world.World
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment

class StatsFragment(val world: World): Fragment {
    val hpTextBox = createTextBox()
    val attackTextBox = createTextBox()
    val defenseTextBox = createTextBox()

    override val root = Components.vbox()
            .withSize(GameConfig.SIDEBAR_WIDTH - 2, 10)
            .withPosition(0, 0)
            .withSpacing(0)
            .build().apply {
                addComponent(Components.header().withText("Stats").build())
                addComponent(hpTextBox)
                addComponent(attackTextBox)
                addComponent(defenseTextBox)
            }

    fun update() {
        hpTextBox.text = "HP: ${world.player.hitpoints}"
        attackTextBox.text = "Attack: ${world.player.attack}"
        defenseTextBox.text = "Defense: ${world.player.defense}"
    }

    companion object {
        fun createTextBox() = Components.label()
                .withSize(GameConfig.SIDEBAR_WIDTH - 2, 1)
                .withText("")
                .build()
    }
}