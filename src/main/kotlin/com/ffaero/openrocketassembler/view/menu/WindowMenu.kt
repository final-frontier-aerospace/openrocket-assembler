package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.ffaero.openrocketassembler.view.ApplicationView
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class WindowMenu(private val view: ApplicationView, private val proj: ProjectController) : JMenu("Window") {
	private val newMenu = JMenuItem("New").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = proj.app.addProject(Project.newBuilder(), null)
		})
		this@WindowMenu.add(this)
	}
	
	private val closeMenu = JMenuItem("Close").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.close()
		})
		this@WindowMenu.add(this)
	}
}
