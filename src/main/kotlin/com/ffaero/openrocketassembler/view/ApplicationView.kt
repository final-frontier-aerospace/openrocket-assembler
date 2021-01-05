package com.ffaero.openrocketassembler.view

import javax.swing.JFrame
import java.awt.event.WindowEvent
import java.awt.Dimension
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import javax.swing.JMenuBar
import java.awt.event.WindowListener
import com.ffaero.openrocketassembler.view.menu.FileMenu
import javax.swing.UIManager
import com.ffaero.openrocketassembler.view.menu.WindowMenu

class ApplicationView(private val view: ViewManager, private val proj: ProjectController) {
	private val frame = JFrame().apply {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
		extendedState = JFrame.MAXIMIZED_BOTH
		preferredSize = Dimension(1024, 768)
		size = preferredSize
		title = "OpenRocket Assembler"
		addWindowListener(object : WindowListener {
			override fun windowActivated(e: WindowEvent?) = Unit
			override fun windowClosed(e: WindowEvent?) = Unit
			override fun windowDeactivated(e: WindowEvent?) = Unit
			override fun windowDeiconified(e: WindowEvent?) = Unit
			override fun windowIconified(e: WindowEvent?) = Unit
			override fun windowOpened(e: WindowEvent?) = Unit
			
			override fun windowClosing(e: WindowEvent?) = close()
		})
	}
	
	private val menu = JMenuBar().apply {
		add(FileMenu(view, proj))
		add(WindowMenu(proj))
		frame.setJMenuBar(this)
	}
	
	public fun close() {
		proj.stop()
	}
	
	init {
		frame.apply {
			setVisible(true)
			toFront()
			requestFocus()
			setState(JFrame.NORMAL)
		}
		proj.addListener(object : ProjectAdapter() {
			override fun onStop(sender: ProjectController) = frame.dispose()
		})
	}
}
