package com.ffaero.openrocketassembler.model

class HistoryStep(private val transact: HistoryTransaction, private val doAction: Runnable) {
    fun toUndo(undoAction: Runnable): HistoryTransaction {
        transact.add(doAction, undoAction)
        return transact
    }
}
