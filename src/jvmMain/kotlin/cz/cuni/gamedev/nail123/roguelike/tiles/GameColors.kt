package cz.cuni.gamedev.nail123.roguelike.tiles

import org.hexworks.zircon.api.color.TileColor

object GameColors {
    val WALL_FOREGROUND = TileColor.fromString("#75715E")
    val WALL_BACKGROUND = TileColor.fromString("#3E3D32")

    val FLOOR_FOREGROUND = TileColor.fromString("#3C3F41")
    val FLOOR_BACKGROUND = TileColor.fromString("#232322")

    val ACCENT_COLOR = TileColor.fromString("#FFCD22")
    val OBJECT_FOREGROUND = TileColor.fromString("#FCA903")

    val STAIRS_FOREGROUND = TileColor.fromString("#00A10D")
    val DOOR_FOREGROUND = TileColor.fromString("#AD6200")

    val BLACK = TileColor.fromString("#000000")
}