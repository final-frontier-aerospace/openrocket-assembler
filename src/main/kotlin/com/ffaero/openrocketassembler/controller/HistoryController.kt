package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.HistoryTransaction

class HistoryController(private val app: ApplicationController) : DispatcherBase<HistoryListener, HistoryListenerList>(HistoryListenerList()) {
    private val history = ArrayDeque<HistoryTransaction>()
    private var index = 0
    private var lastSavedIndex = 0
    private var lastFileModified = false

    val fileModified: Boolean
            get() = index != lastSavedIndex

    val undoAction: String?
            get() = if (index > 0) {
                history[index - 1].desc
            } else {
                null
            }

    val redoAction: String?
            get() = if (index < history.size) {
                history[index].desc
            } else {
                null
            }

    private fun dispatchEvent() {
        if (fileModified != lastFileModified) {
            lastFileModified = fileModified
            listener.onStatus(this, lastFileModified)
        }
        listener.onHistoryUpdate(this, undoAction, redoAction)
    }

    fun undo() {
        if (index > 0) {
            val ent = history[index - 1]
            ent.undo()
            --index
            dispatchEvent()
        }
    }

    fun redo() {
        if (index < history.size) {
            val ent = history[index]
            ent.run()
            ++index
            dispatchEvent()
        }
    }

    private fun trim() {
        while (history.size > app.settings.historyLength) {
            history.removeFirst()
            --index
            --lastSavedIndex
        }
    }

    internal fun perform(transact: HistoryTransaction) {
        transact.run()
        while (history.size > index) {
            history.removeLast()
        }
        trim()
        history.add(transact)
        index = history.size
        if (lastSavedIndex >= index) {
            lastSavedIndex = -1
        }
        dispatchEvent()
    }

    internal fun reset() {
        history.clear()
        index = 0
        lastSavedIndex = 0
        dispatchEvent()
    }

    internal fun afterSave() {
        lastSavedIndex = history.size
        dispatchEvent()
    }

    init {
        app.settings.addListener(object : SettingAdapter() {
            override fun onSettingsUpdated(sender: SettingController) = trim()
        })
    }
}
