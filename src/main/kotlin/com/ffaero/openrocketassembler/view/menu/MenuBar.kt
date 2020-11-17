package com.ffaero.openrocketassembler.view.menu

import javax.swing.JFrame
import com.ffaero.openrocketassembler.controller.ApplicationController
import javax.swing.JMenuBar

class MenuBar(app: ApplicationController, frame: JFrame) {
	private val menu = JMenuBar().apply {
		FileMenu(app, this)
		EditMenu(app, this)
		ComponentMenu(app, this)
		ConfigurationMenu(app, this)
		WindowMenu(app, this)
		frame.setJMenuBar(this)
	}
}
