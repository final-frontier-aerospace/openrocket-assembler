package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.ActionListener
import javax.swing.JMenuBar
import javax.swing.KeyStroke
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import java.awt.event.KeyEvent

class ConfigurationMenu(app: ApplicationController, menuBar: JMenuBar) {
	private val newMenu = JMenuItem("New").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val menu = JMenu("Component").apply {
		setMnemonic('m')
		add(newMenu)
		addSeparator()
		menuBar.add(this)
	}
}