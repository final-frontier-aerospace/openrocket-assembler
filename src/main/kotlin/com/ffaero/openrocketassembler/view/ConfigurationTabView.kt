package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ConfigurationController
import javax.swing.JTabbedPane
import com.ffaero.openrocketassembler.controller.ConfigurationAdapter
import javax.swing.JPanel
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JOptionPane
import javax.swing.event.ChangeListener
import javax.swing.event.ChangeEvent
import javax.swing.JScrollPane

class ConfigurationTabView(private val proj: ProjectController) : JTabbedPane() {
	private var updating = false
	private final val configListener = object : ConfigurationAdapter() {
		override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) {
			while (getTabCount() > 1) {
				removeTabAt(0)
			}
			var idx = 0
			updating = true
			names.forEach {
				insertTab("", null, RocketList(proj), "", idx)
				setTabComponentAt(idx, ConfigurationTabLabel().apply {
					text = it
				})
				++idx
			}
			setSelectedIndex(0)
			updating = false
		}

		override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String) {
			updating = true
			insertTab("", null, RocketList(proj), "", index)
			setTabComponentAt(index, ConfigurationTabLabel().apply {
				text = name
			})
			setSelectedIndex(index)
			updating = false
		}

		override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) {
			updating = true
			removeTabAt(index)
			updating = false
		}

		override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) {
			updating = true
			val comp = getTabComponentAt(toIndex)
			if (!(comp is ConfigurationTabLabel)) {
				return
			}
			onConfigurationRemoved(sender, fromIndex)
			onConfigurationAdded(sender, toIndex, comp.text)
			updating = false
		}

		override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) {
			val comp = getTabComponentAt(index)
			if (!(comp is ConfigurationTabLabel)) {
				return
			}
			comp.text = name
		}
	}
	
	private fun addConfig() {
		val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a name for the new configuration:", "Create", JOptionPane.QUESTION_MESSAGE)
		if (str != null && !str.isEmpty()) {
			proj.configurations.add(str)
		}
	}
	
	init {
		addTab("", JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))
		setTabComponentAt(0, ConfigurationTabLabel().apply {
			text = "+"
			setForeground(Color.gray)
			addMouseListener(object : MouseAdapter() {
				override fun mouseClicked(e: MouseEvent?) {
					if (e == null) {
						return
					}
					if (e.getButton() == MouseEvent.BUTTON1) {
						addConfig()
					}
				}
			})
		})
		addChangeListener(object : ChangeListener {
			private var lastTab = 0
			
			override fun stateChanged(e: ChangeEvent?) {
				if (updating) {
					return
				}
				val tab = getSelectedIndex()
				if (tab > 0 && tab == getTabCount() - 1) {
					setSelectedIndex(lastTab)
					addConfig()
				} else {
					lastTab = tab
				}
			}
		})
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				configListener.onConfigurationsReset(proj.configurations, proj.configurations.names)
				proj.configurations.addListener(configListener)
			}

			override fun removeListeners() {
				proj.configurations.removeListener(configListener)
			}
		})
	}
}
