package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.view.ViewManager
import com.ffaero.openrocketassembler.controller.ProjectController
import java.awt.event.ActionListener
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JMenuItem

class FileMenu(private val view: ViewManager, private val proj: ProjectController) : JMenu("File") {
	private val exitMenu = JMenuItem("Exit").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.exit()
		})
		this@FileMenu.add(this)
	}
}
