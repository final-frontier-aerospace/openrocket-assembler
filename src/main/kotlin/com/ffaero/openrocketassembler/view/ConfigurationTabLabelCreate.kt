package com.ffaero.openrocketassembler.view

import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JScrollPane

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
