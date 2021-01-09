package com.ffaero.openrocketassembler.controller

interface HistoryListener {
    fun onStatus(sender: HistoryController, modified: Boolean)
    fun onHistoryUpdate(sender: HistoryController, undoAction: String?, redoAction: String?)
}
