package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.*
import com.ffaero.openrocketassembler.view.ListenerLifecycleManager
import java.awt.EventQueue
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JRadioButtonMenuItem

class OpenRocketMenu(private val proj: ProjectController) : JMenu("OpenRocket") {
	init {
		JMenuItem("Launch").apply {
			addActionListener { proj.app.openrocket.launch(proj.openRocketVersion) }
			this@OpenRocketMenu.add(this)
			this@OpenRocketMenu.addSeparator()
		}

		val openRocketListener: OpenRocketListener
		val projectListener: ProjectListener

		JMenu("Set OpenRocket Version").apply {
			val list = ArrayList<JRadioButtonMenuItem>()
			val setListener = ActionListener { e ->
				if (e != null) {
					proj.openRocketVersion = e.actionCommand
				}
			}
			openRocketListener = object : OpenRocketAdapter() {
				override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) {
					EventQueue.invokeLater {
						removeAll()
						versions.forEach {
							add(JRadioButtonMenuItem(it).apply {
								actionCommand = it
								isSelected = it == proj.openRocketVersion
								addActionListener(setListener)
								list.add(this)
							})
						}
					}
				}
			}
			projectListener = object : ProjectAdapter() {
				override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
					EventQueue.invokeLater {
						list.forEach {
							it.isSelected = it.actionCommand == version
						}
					}
				}
			}
			this@OpenRocketMenu.add(this)
		}

		JMenuItem("Check for Updates").apply {
			addActionListener { proj.app.openrocket.checkForUpdates() }
			this@OpenRocketMenu.add(this)
		}

		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				openRocketListener.onOpenRocketVersionsUpdated(proj.app.openrocket, proj.app.openrocket.versions)
				proj.app.openrocket.addListener(openRocketListener)
				projectListener.onOpenRocketVersionChange(proj, proj.openRocketVersion)
				proj.addListener(projectListener)
			}

			override fun removeListeners() {
				proj.app.openrocket.removeListener(openRocketListener)
				proj.removeListener(projectListener)
			}
		})
	}
}
