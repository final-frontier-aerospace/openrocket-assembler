package com.ffaero.openrocketassembler.view

import java.awt.EventQueue
import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import javax.swing.JPanel
import javax.swing.JLabel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory

class StatusBar(private val app: ApplicationController) : JPanel() {
	private val appListener = object : ApplicationAdapter() {
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
	}
	
	private val label = JLabel().apply {
		setForeground(Color.black)
	}
	
	init {
		setBackground(Color(224, 224, 224))
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10))
		setLayout(BorderLayout())
		add(label, BorderLayout.CENTER)
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				app.addListener(appListener)
			}

			override fun removeListeners() {
				app.removeListener(appListener)
			}
		})
	}
}
