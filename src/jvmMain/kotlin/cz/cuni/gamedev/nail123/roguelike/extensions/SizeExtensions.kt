package cz.cuni.gamedev.nail123.roguelike.extensions

import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

fun Size3D.allPositionsShuffled() =
        (0 until xLength * yLength * zLength).shuffled().asSequence().map { indexTo3d(it, this) }

fun indexTo3d(i: Int, size: Size3D) = Position3D.create(
        i % size.xLength,
        (i % (size.xLength * size.yLength)) / size.xLength,
        i / (size.xLength * size.yLength)
)