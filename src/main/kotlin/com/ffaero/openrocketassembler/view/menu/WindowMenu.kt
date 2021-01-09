package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.ffaero.openrocketassembler.view.ApplicationView
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class WindowMenu(private val view: ApplicationView, private val proj: ProjectController) : JMenu("Window") {
	init {
		JMenuItem("New").apply {
			addActionListener { proj.app.addProject(Project.newBuilder(), null) }
			this@WindowMenu.add(this)
		}

		JMenuItem("Close").apply {
			accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK)
			addActionListener { view.close() }
			this@WindowMenu.add(this)
		}
	}
}
