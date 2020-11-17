package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.ActionListener
import javax.swing.JMenuBar
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JMenuItem

class WindowMenu(app: ApplicationController, menuBar: JMenuBar) {
	private val exitMenu = JMenuItem("Exit").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				app.exit()
			}
		})
	}
	
	private val menu = JMenu("Window").apply {
		setMnemonic('W')
		add(exitMenu)
		menuBar.add(this)
	}
}
