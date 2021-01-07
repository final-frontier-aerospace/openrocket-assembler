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
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

class ConfigurationTabView(internal val proj: ProjectController) : JTabbedPane() {
	private var updating = false
	
	private val configListener = object : ConfigurationAdapter() {
		override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) {
			while (getTabCount() > 1) {
				removeTabAt(0)
			}
			var idx = 0
			updating = true
			names.forEach {
				ConfigurationTabLabel(this@ConfigurationTabView, it).insert(this@ConfigurationTabView, idx++)
			}
			setSelectedIndex(0)
			updating = false
		}

		override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String) {
			updating = true
			ConfigurationTabLabel(this@ConfigurationTabView, name).insert(this@ConfigurationTabView, index)
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
			val comp = getTabComponentAt(fromIndex)
			if (!(comp is ConfigurationTabLabelBase)) {
				return
			}
			onConfigurationRemoved(sender, fromIndex)
			onConfigurationAdded(sender, toIndex, comp.text)
			updating = false
		}

		override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) {
			val comp = getTabComponentAt(index)
			if (!(comp is ConfigurationTabLabelBase)) {
				return
			}
			comp.text = name
		}
	}
	
	private fun getTab(e: ActionEvent?): ConfigurationTabLabel? {
		if (e == null) {
			return null
		}
		val src = e.getSource()
		if (!(src is JMenuItem)) {
			return null
		}
		val ctx = src.getParent()
		if (!(ctx is JPopupMenu)) {
			return null
		}
		val inv = ctx.getInvoker()
		if (!(inv is ConfigurationTabLabel)) {
			return null
		}
		return inv
	}
	
	internal val tabMouseListener = object : MouseAdapter() {
		private val contextMenu = JPopupMenu().apply {
			add(JMenuItem("Duplicate").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val tab = getTab(e)
						if (tab != null) {
							val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a name for the duplicated configuration:", "Duplicate", JOptionPane.QUESTION_MESSAGE)
							if (str != null && !str.isEmpty()) {
								proj.configurations.duplicate(indexOfTabComponent(tab), str)
							}
						}
					}
				})
			})
			add(JMenuItem("Delete").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val tab = getTab(e)
						if (tab != null) {
							proj.configurations.remove(indexOfTabComponent(tab))
						}
					}
				})
			})
			addSeparator()
			add(JMenuItem("Move Left").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val tab = getTab(e)
						if (tab != null) {
							val idx = indexOfTabComponent(tab)
							if (idx > 0) {
								proj.configurations.move(idx, idx - 1)
							}
						}
					}
				})
			})
			add(JMenuItem("Move Right").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val tab = getTab(e)
						if (tab != null) {
							val idx = indexOfTabComponent(tab)
							if (idx < getTabCount() - 2) {
								proj.configurations.move(idx, idx + 1)
							}
						}
					}
				})
			})
		}
		
		override fun mouseReleased(e: MouseEvent?) {
			if (e == null) {
				return
			}
			if (e.isPopupTrigger()) {
				contextMenu.show(e.getComponent(), e.getX(), e.getY())
			}
		}
	}
	
	public fun addConfig() {
		val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a name for the new configuration:", "Create", JOptionPane.QUESTION_MESSAGE)
		if (str != null && !str.isEmpty()) {
			proj.configurations.add(str)
		}
	}
	
	init {
		ConfigurationTabLabelCreate(this).insert(this, 0)
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
