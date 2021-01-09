package com.ffaero.openrocketassembler.controller

class HistoryListenerList : ListenerListBase<HistoryListener>(), HistoryListener {
    override fun onStatus(sender: HistoryController, modified: Boolean) = forEach { it.onStatus(sender, modified) }
    override fun onHistoryUpdate(sender: HistoryController, undoAction: String?, redoAction: String?) = forEach { it.onHistoryUpdate(sender, undoAction, redoAction) }
}
