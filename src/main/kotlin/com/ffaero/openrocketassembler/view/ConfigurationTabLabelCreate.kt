package com.ffaero.openrocketassembler.view

import java.awt.event.MouseAdapter
import javax.swing.JScrollPane
import java.awt.event.MouseEvent
import java.awt.Color

class ConfigurationTabLabelCreate(private val tabs: ConfigurationTabView) : ConfigurationTabLabelBase(JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)) {
	init {
		text = "+"
		setForeground(Color.gray)
		addMouseListener(object : MouseAdapter() {
			override fun mouseClicked(e: MouseEvent?) {
				if (e == null) {
					return
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					tabs.addConfig()
				}
			}
		})
	}
}
