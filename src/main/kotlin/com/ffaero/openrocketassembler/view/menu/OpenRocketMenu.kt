package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.view.RedirectedEventQueue
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

class OpenRocketMenu(private val parent: Component, private val proj: ProjectController) : JMenu("OpenRocket") {
	private val launchMenu = JMenuItem("Launch").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = proj.app.openrocket.launch(proj.openRocketVersion)
		})
		this@OpenRocketMenu.add(this)
		this@OpenRocketMenu.addSeparator()
	}
	
	private val setMenu = JMenu("Set OpenRocket Version").apply {
		val list = ArrayList<JRadioButtonMenuItem>()
		val setListener = object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				if (e != null) {
					proj.openRocketVersion = e.getActionCommand()
				}
			}
		}
		proj.app.openrocket.addListener(object : OpenRocketAdapter() {
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
		})
		proj.addListener(object : ProjectAdapter() {
			override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
				list.forEach {
					it.setSelected(it.getActionCommand().equals(version))
				}
			}
		}.apply {
			onOpenRocketVersionChange(proj, proj.openRocketVersion)
		})
		this@OpenRocketMenu.add(this)
	}
	
	private val updateMenu = JMenuItem("Check for Updates").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				try {
					RedirectedEventQueue().use {
						proj.app.openrocket.checkForUpdates()
					}
				} catch (ex: IOException) {
					JOptionPane.showMessageDialog(parent, "Error while checking for OpenRocket updates:\n" + ex.message, "Check for Updates", JOptionPane.ERROR_MESSAGE)
				}
			}
		})
		this@OpenRocketMenu.add(this)
	}
}
