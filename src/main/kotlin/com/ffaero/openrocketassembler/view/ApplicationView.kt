package com.ffaero.openrocketassembler.view

import javax.swing.JFrame
import java.awt.event.WindowEvent
import java.awt.Dimension
import com.ffaero.openrocketassembler.controller.ApplicationControllerAdapter
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.WindowListener
import javax.swing.UIManager
import com.ffaero.openrocketassembler.view.menu.MenuBar

class ApplicationView(private val app: ApplicationController) {
	private val frame: JFrame = JFrame().apply {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
		extendedState = JFrame.MAXIMIZED_BOTH
		preferredSize = Dimension(1024, 768)
		size = preferredSize
		MenuBar(app, this)
		addWindowListener(object : WindowListener {
			override fun windowActivated(e: WindowEvent?) = Unit
			override fun windowClosed(e: WindowEvent?) = Unit
			override fun windowDeactivated(e: WindowEvent?) = Unit
			override fun windowDeiconified(e: WindowEvent?) = Unit
			override fun windowIconified(e: WindowEvent?) = Unit
			override fun windowOpened(e: WindowEvent?) = Unit

			override fun windowClosing(e: WindowEvent?) {
				app.exit()
			}
		})
	}
	
	init {
		app.addListener(object : ApplicationControllerAdapter() {
			override fun onStart(sender: ApplicationController) {
				frame.setVisible(true)
			}
	
			override fun onStop(sender: ApplicationController) {
				frame.dispose()
			}
	
			override fun onProjectChange(sender: ProjectController, name: String?, modified: Boolean) {
				if (name !== null) {
					if (modified) {
						frame.title = "OpenRocket Assembler - ${name} (*)"
					} else {
						frame.title = "OpenRocket Assembler - ${name}"
					}
				} else {
					frame.title = "OpenRocket Assembler"
				}
			}
		}.apply {
			onProjectChange(app.project, null, false)
		})
	}
}
