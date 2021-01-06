package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ProjectController
import java.awt.event.ActionListener
import java.io.IOException
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JOptionPane
import javax.swing.JMenuItem
import java.awt.Component
import com.ffaero.openrocketassembler.controller.OpenRocketAdapter
import com.ffaero.openrocketassembler.controller.OpenRocketController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import javax.swing.JRadioButtonMenuItem
import com.ffaero.openrocketassembler.controller.OpenRocketListener
import com.ffaero.openrocketassembler.controller.ProjectListener
import java.awt.event.HierarchyListener
import java.awt.event.HierarchyEvent
import java.awt.Container
import com.ffaero.openrocketassembler.view.ListenerLifecycleManager

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
		}.apply {
			onOpenRocketVersionsUpdated(proj.app.openrocket, proj.app.openrocket.versions)
		}
		projectListener = object : ProjectAdapter() {
			override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
				list.forEach {
					it.setSelected(it.getActionCommand().equals(version))
				}
			}
		}.apply {
			onOpenRocketVersionChange(proj, proj.openRocketVersion)
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
				proj.app.openrocket.addListener(openRocketListener)
				proj.addListener(projectListener)
			}

			override fun removeListeners() {
				proj.app.openrocket.removeListener(openRocketListener)
				proj.removeListener(projectListener)
			}
		})
	}
}
