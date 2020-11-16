package com.ffaero.openrocketassembler.ui

import javax.swing.JFrame
import java.awt.Dimension
import javax.swing.UIManager

class MainWindow {
	private val frame: JFrame;
	
	public constructor() {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		frame = JFrame().apply {
			extendedState = JFrame.MAXIMIZED_BOTH
			preferredSize = Dimension(1024, 768)
			size = preferredSize
			title = "OpenRocket Assembler"
		}
		frame.setVisible(true)
	}
}
