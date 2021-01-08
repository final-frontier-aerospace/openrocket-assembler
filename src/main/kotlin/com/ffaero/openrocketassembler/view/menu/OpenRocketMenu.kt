package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.OpenRocketAdapter
import com.ffaero.openrocketassembler.controller.OpenRocketController
import com.ffaero.openrocketassembler.controller.OpenRocketListener
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectListener
import com.ffaero.openrocketassembler.view.ListenerLifecycleManager
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.HierarchyListener
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JRadioButtonMenuItem

class OpenRocketMenu(private val parent: Component, private val proj: ProjectController) : JMenu("OpenRocket") {
	private val launchMenu = JMenuItem("Launch").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = proj.app.openrocket.launch(proj.openRocketVersion)
		})
		this@OpenRocketMenu.add(this)
		this@OpenRocketMenu.addSeparator()
	}
	
	private val openRocketListener: OpenRocketListener
	private val projectListener: ProjectListener
	
	private val setMenu = JMenu("Set OpenRocket Version").apply {
		val list = ArrayList<JRadioButtonMenuItem>()
		val setListener = object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				if (e != null) {
					proj.openRocketVersion = e.getActionCommand()
				}
			}
		}
		openRocketListener = object : OpenRocketAdapter() {
			override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) {
				removeAll()
				versions.forEach {
					add(JRadioButtonMenuItem(it).apply {
						setActionCommand(it)
						setSelected(it.equals(proj.openRocketVersion))
						addActionListener(setListener)
						list.add(this)
					})
				}
			}
		}
		projectListener = object : ProjectAdapter() {
			override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
				list.forEach {
					it.setSelected(it.getActionCommand().equals(version))
				}
			}
		}
		this@OpenRocketMenu.add(this)
	}
	
	private val updateMenu = JMenuItem("Check for Updates").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				proj.app.openrocket.checkForUpdates()
			}
		})
		this@OpenRocketMenu.add(this)
	}
	
	init {
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
