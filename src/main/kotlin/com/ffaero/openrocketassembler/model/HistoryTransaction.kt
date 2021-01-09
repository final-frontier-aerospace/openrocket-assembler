package com.ffaero.openrocketassembler.model

class HistoryTransaction(val desc: String) {
    private val doActions = ArrayList<Runnable>()
    private val undoActions = ArrayList<Runnable>()

    fun add(doAction: Runnable): HistoryStep = HistoryStep(this, doAction)

    internal fun add(doAction: Runnable, undoAction: Runnable) {
        doActions.add(doAction)
        undoActions.add(undoAction)
    }

    fun run() = doActions.forEach { it.run() }
    fun undo() = undoActions.reversed().forEach { it.run() }
}
