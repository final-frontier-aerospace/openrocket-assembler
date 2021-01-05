package com.ffaero.openrocketassembler.view

import javax.swing.JFrame
import java.awt.event.WindowEvent
import java.awt.Dimension
import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import javax.swing.JMenuBar
import java.awt.event.WindowListener
import com.ffaero.openrocketassembler.view.menu.FileMenu
import javax.swing.UIManager

class ApplicationView(private val app: ApplicationController) {
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
			
			override fun windowClosing(e: WindowEvent?) = app.stop()
		})
	}
	
	private val menu = JMenuBar().apply {
		add(FileMenu(app))
		frame.setJMenuBar(this)
	}
	
	init {
		app.addListener(object : ApplicationAdapter() {
			override fun onStart(sender: ApplicationController) {
				frame.apply {
					setVisible(true)
					toFront()
					requestFocus()
					setState(JFrame.NORMAL)
				}
			}
			
			override fun onStop(sender: ApplicationController) = frame.dispose()
		})
	}
}
