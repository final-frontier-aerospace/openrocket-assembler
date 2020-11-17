package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.ActionListener
import javax.swing.JMenuBar
import javax.swing.KeyStroke
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import java.awt.event.KeyEvent

class EditMenu(app: ApplicationController, menuBar: JMenuBar) {
	private val undoMenu = JMenuItem("Undo").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val redoMenu = JMenuItem("Redo").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val menu = JMenu("Edit").apply {
		setMnemonic('E')
		add(undoMenu)
		add(redoMenu)
		menuBar.add(this)
	}
}
