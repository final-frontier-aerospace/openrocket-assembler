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
import java.io.File
import javax.swing.event.PopupMenuListener
import javax.swing.event.PopupMenuEvent

class ConfigurationTabView(internal val proj: ProjectController) : JTabbedPane() {
	private var updating = false
	
	private val configListener = object : ConfigurationAdapter() {
		override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) {
			while (getTabCount() > 1) {
				removeTabAt(0)
			}
			updating = true
			names.forEachIndexed { idx, it ->
				ConfigurationTabLabel(this@ConfigurationTabView, it).apply {
					val view = view
					if (view is RocketList) {
						view.onReset(sender.componentsAt(idx))
					}
					insert(this@ConfigurationTabView, idx)
				}
			}
			setSelectedIndex(0)
			updating = false
		}

		override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String, components: List<File>) {
			updating = true
			ConfigurationTabLabel(this@ConfigurationTabView, name).apply {
				val view = view
				if (view is RocketList) {
					view.onReset(components)
				}
				insert(this@ConfigurationTabView, index)
			}
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
			removeTabAt(fromIndex)
			comp.insert(this@ConfigurationTabView, toIndex)
			setSelectedIndex(toIndex)
			updating = false
		}

		override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) {
			val comp = getTabComponentAt(index)
			if (!(comp is ConfigurationTabLabelBase)) {
				return
			}
			comp.text = name
		}
		override fun onComponentAdded(sender: ConfigurationController, configIndex: Int, index: Int, component: File) {
			val comp = getComponentAt(configIndex)
			if (!(comp is RocketList)) {
				return
			}
			comp.onAdd(index, component)
		}

		override fun onComponentRemoved(sender: ConfigurationController, configIndex: Int, index: Int) {
			val comp = getComponentAt(configIndex)
			if (!(comp is RocketList)) {
				return
			}
			comp.onRemove(index)
		}

		override fun onComponentMoved(sender: ConfigurationController, configIndex: Int, fromIndex: Int, toIndex: Int) {
			val comp = getComponentAt(configIndex)
			if (!(comp is RocketList)) {
				return
			}
			comp.onMove(fromIndex, toIndex)
		}

		override fun onComponentChanged(sender: ConfigurationController, configIndex: Int, index: Int, component: File) {
			val comp = getComponentAt(configIndex)
			if (!(comp is RocketList)) {
				return
			}
			comp.onChange(index, component)
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
			add(JMenuItem("Select").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val tab = getTab(e)
						if (tab != null) {
							setSelectedIndex(indexOfTabComponent(tab))
						}
					}
				})
			})
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
	
	private fun getItem(e: ActionEvent?): RocketListItem? {
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
		if (!(inv is RocketListItem)) {
			return null
		}
		return inv
	}
	
	internal val componentMouseListener = object : MouseAdapter() {
		private val contextMenu = JPopupMenu().apply {
			add(JMenuItem("Delete").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e)
						if (src == null) {
							return
						}
						proj.configurations.removeComponent(indexOfComponent(src.list), src.index)
					}
				})
			})
			addSeparator()
			add(JMenuItem("Move Up").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e)
						if (src == null) {
							return
						}
						if (src.index > 0) {
							proj.configurations.moveComponent(indexOfComponent(src.list), src.index, src.index - 1)
						}
					}
				})
			})
			add(JMenuItem("Move Down").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e)
						if (src == null) {
							return
						}
						val idx = indexOfComponent(src.list)
						if (src.index < proj.configurations.componentsAt(idx).size - 1) {
							proj.configurations.moveComponent(idx, src.index, src.index + 1)
						}
					}
				})
			})
			addPopupMenuListener(object : PopupMenuListener {
				private fun item(e: PopupMenuEvent?): RocketListItem? {
					if (e == null) {
						return null
					}
					val src = e.getSource()
					if (!(src is JPopupMenu)) {
						return null
					}
					val item = src.getInvoker()
					if (!(item is RocketListItem)) {
						return null
					}
					return item
				}
				
				override fun popupMenuCanceled(e: PopupMenuEvent?) = Unit

				override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent?) {
					item(e)?.hoverEnd()
				}

				override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
					item(e)?.hoverStart()
				}
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
	
	public fun addComponentToCurrent(component: File) {
		val idx = getSelectedIndex()
		if (idx < getTabCount() - 1) {
			proj.configurations.addComponent(idx, proj.configurations.componentsAt(idx).size, component)
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
