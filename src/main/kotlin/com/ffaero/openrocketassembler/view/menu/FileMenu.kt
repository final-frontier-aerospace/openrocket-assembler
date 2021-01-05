package com.ffaero.openrocketassembler.view.menu

import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.KeyStroke
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import java.awt.event.KeyEvent
import com.ffaero.openrocketassembler.controller.ApplicationController

class FileMenu(app: ApplicationController) : JMenu("File") {
	private val exitMenu = JMenuItem("Exit").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = app.stop()
		})
		this@FileMenu.add(this)
	}
}
