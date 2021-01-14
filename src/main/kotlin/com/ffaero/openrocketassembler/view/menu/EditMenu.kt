package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.HistoryAdapter
import com.ffaero.openrocketassembler.controller.HistoryController
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.view.ListenerLifecycleManager
import java.awt.EventQueue
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class EditMenu(private val proj: ProjectController) : JMenu("Edit") {
    private val historyListener = object : HistoryAdapter() {
        override fun onHistoryUpdate(sender: HistoryController, undoAction: String?, redoAction: String?) {
            EventQueue.invokeLater {
                undo.isEnabled = undoAction != null
                undo.text = "Undo " + (undoAction ?: "")
                redo.isEnabled = redoAction != null
                redo.text = "Redo " + (redoAction ?: "")
            }
        }
    }

    private val undo = JMenuItem().apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK)
        addActionListener { proj.history.undo() }
        this@EditMenu.add(this)
    }

    private val redo = JMenuItem().apply {
        accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK)
        addActionListener { proj.history.redo() }
        this@EditMenu.add(this)
    }

    init {
        addHierarchyListener(object : ListenerLifecycleManager() {
            override fun addListeners() {
                historyListener.onHistoryUpdate(proj.history, proj.history.undoAction, proj.history.redoAction)
                proj.history.addListener(historyListener)
            }

            override fun removeListeners() {
                proj.history.removeListener(historyListener)
            }
        })
    }
}
