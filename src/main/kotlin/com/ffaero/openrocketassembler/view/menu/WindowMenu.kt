package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.view.ViewManager
import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.KeyStroke
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import java.awt.event.KeyEvent
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.view.ApplicationView

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
