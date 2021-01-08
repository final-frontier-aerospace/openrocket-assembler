package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.BorderLayout
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel

class StatusBar(private val app: ApplicationController) : JPanel() {
	private var actuallyVisible = false
	
	private val label = JLabel().apply {
		setText("*")
		setForeground(Color.black)
	}
	
	private val appListener = object : ApplicationAdapter() {
		override fun onBackgroundStatus(sender: ApplicationController, status: String) {
			EventQueue.invokeLater {
				actuallyVisible = !status.isEmpty()
				if (actuallyVisible) {
					label.setText(status)
				} else {
					label.setText("*")
				}
			}
		}
	}
	
	override fun paint(g: Graphics?) {
		if (actuallyVisible) {
			super.paint(g)
		}
	}
	
	init {
		setBackground(Color(224, 224, 224))
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10))
		setLayout(BorderLayout())
		add(label, BorderLayout.CENTER)
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				appListener.onBackgroundStatus(app, app.backgroundStatus)
				app.addListener(appListener)
			}

			override fun removeListeners() {
				app.removeListener(appListener)
			}
		})
	}
}
