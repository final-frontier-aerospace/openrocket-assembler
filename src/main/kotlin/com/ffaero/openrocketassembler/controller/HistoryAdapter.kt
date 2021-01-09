package com.ffaero.openrocketassembler.controller

open class HistoryAdapter : HistoryListener {
    override fun onStatus(sender: HistoryController, modified: Boolean) = Unit
    override fun onHistoryUpdate(sender: HistoryController, undoAction: String?, redoAction: String?) = Unit
}
