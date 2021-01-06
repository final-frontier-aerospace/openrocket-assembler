package com.ffaero.openrocketassembler.view

import javax.swing.JPanel
import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.Color
import javax.swing.JLabel
import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import java.awt.EventQueue
import java.awt.BorderLayout
import javax.swing.BorderFactory

class StatusBar(private val app: ApplicationController) : JPanel() {
	private val label = JLabel().apply {
		setForeground(Color.black)
	}
	
	init {
		setBackground(Color(224, 224, 224))
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10))
		setLayout(BorderLayout())
		add(label, BorderLayout.CENTER)
		app.addListener(object : ApplicationAdapter() {
			override fun onBackgroundStatus(sender: ApplicationController, status: String) {
				EventQueue.invokeLater {
					if (status.isEmpty()) {
						setVisible(false)
					} else {
						label.setText(status)
						setVisible(true)
					}
				}
			}
		}.apply {
			onBackgroundStatus(app, app.backgroundStatus)
		})
	}
}
