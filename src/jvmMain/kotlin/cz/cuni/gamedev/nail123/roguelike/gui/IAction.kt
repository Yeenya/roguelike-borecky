package cz.cuni.gamedev.nail123.roguelike.gui

interface IAction<in T> {
    fun tryPerform(parameter: T): Boolean
}