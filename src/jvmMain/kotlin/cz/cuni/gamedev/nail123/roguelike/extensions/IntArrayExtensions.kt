package cz.cuni.gamedev.nail123.roguelike.extensions

import org.mifek.wfc.datastructures.IntArray2D

fun List<String>.toIntArray2D(): IntArray2D {
    val intArray = IntArray2D(this[0].length, this.count())
    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            intArray[x, y] = c.code
        }
    }
    return intArray
}