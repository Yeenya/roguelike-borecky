package cz.cuni.gamedev.nail123.roguelike.entities

import cz.cuni.gamedev.nail123.roguelike.blocks.GameBlock
import cz.cuni.gamedev.nail123.roguelike.world.Area
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Tile

abstract class GameEntity(startingTile: Tile = Tile.empty(), val sortingLayer: SortingLayer = SortingLayer.ITEM) {
    val area: Area
        get() = block.area
    lateinit var block: GameBlock

    val tileProperty = createPropertyFrom(startingTile)
    open var tile
        get() = tileProperty.value
        set(value) { tileProperty.value = value }

    // Properties allow binding with other properties and also listen to changes
    val positionProperty = createPropertyFrom(Position3D.unknown())
    var position
        get() = positionProperty.value
        set(value) { positionProperty.value = value }

    val x: Int
        get() = position.x
    val y: Int
        get() = position.y
    val z: Int
        get() = position.z

    abstract val blocksMovement: Boolean
    abstract val blocksVision: Boolean

    init {
        tileProperty.onChange { block.updateTileMap() }
    }

    // Gets called after the area is built
    open fun init() {}
    open fun update() {}

    override fun toString(): String = this.javaClass.simpleName
}