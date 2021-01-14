package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.*
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JTabbedPane
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener

class ConfigurationTabView(internal val proj: ProjectController) : JTabbedPane() {
	companion object {
		private val log = LoggerFactory.getLogger(ConfigurationTabView::class.java)
	}

	private var updating = false
	
	private val configListener = object : ConfigurationAdapter() {
		override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) {
			EventQueue.invokeLater {
				updating = true
				while (tabCount > 1) {
					removeTabAt(0)
				}
				names.forEachIndexed { idx, it ->
					ConfigurationTabLabel(this@ConfigurationTabView, it).apply {
						val view = view
						if (view is RocketList) {
							view.onReset(sender.componentsAt(idx))
						} else {
							log.warn("Invalid view: {}", view)
						}
						enableUnsafeUI = proj.app.settings.enableUnsafeUI
						insert(this@ConfigurationTabView, idx)
					}
				}
				selectedIndex = 0
				updating = false
			}
		}

		override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String, components: List<File>) {
			EventQueue.invokeLater {
				updating = true
				ConfigurationTabLabel(this@ConfigurationTabView, name).apply {
					val view = view
					if (view is RocketList) {
						view.onReset(components)
					} else {
						log.warn("Invalid view: {}", view)
					}
					enableUnsafeUI = proj.app.settings.enableUnsafeUI
					insert(this@ConfigurationTabView, index)
				}
				selectedIndex = index
				updating = false
			}
		}

		override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) {
			EventQueue.invokeLater {
				updating = true
				removeTabAt(index)
				if (index == tabCount - 1 && index > 0) {
					selectedIndex = index - 1
				}
				updating = false
			}
		}

		override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) {
			EventQueue.invokeLater {
				updating = true
				val comp = getTabComponentAt(fromIndex)
				if (comp !is ConfigurationTabLabelBase) {
					log.warn("Invalid tab component: {}", comp)
					return@invokeLater
				}
				removeTabAt(fromIndex)
				comp.insert(this@ConfigurationTabView, toIndex)
				selectedIndex = toIndex
				updating = false
			}
		}

		override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) {
			EventQueue.invokeLater {
				val comp = getTabComponentAt(index)
				if (comp !is ConfigurationTabLabelBase) {
					log.warn("Invalid tab component: {}", comp)
					return@invokeLater
				}
				comp.text = name
			}
		}

		override fun onComponentAdded(sender: ConfigurationController, configIndex: Int, index: Int, component: File) {
			val comp = getComponentAt(configIndex)
			if (comp !is RocketList) {
				log.warn("Invalid component: {}", comp)
				return
			}
			comp.onAdd(index, component)
		}

		override fun onComponentRemoved(sender: ConfigurationController, configIndex: Int, index: Int) {
			val comp = getComponentAt(configIndex)
			if (comp !is RocketList) {
				log.warn("Invalid component: {}", comp)
				return
			}
			comp.onRemove(index)
		}

		override fun onComponentMoved(sender: ConfigurationController, configIndex: Int, fromIndex: Int, toIndex: Int) {
			val comp = getComponentAt(configIndex)
			if (comp !is RocketList) {
				log.warn("Invalid component: {}", comp)
				return
			}
			comp.onMove(fromIndex, toIndex)
		}

		override fun onComponentChanged(sender: ConfigurationController, configIndex: Int, index: Int, component: File) {
			val comp = getComponentAt(configIndex)
			if (comp !is RocketList) {
				log.warn("Invalid component: {}", comp)
				return
			}
			comp.onChange(index, component)
		}
	}

	private val settingsListener = object : SettingAdapter() {
		override fun onSettingsUpdated(sender: SettingController) {
			EventQueue.invokeLater {
				for (i in 0 until tabCount) {
					val comp = getTabComponentAt(i)
					if (comp is ConfigurationTabLabelBase) {
						comp.enableUnsafeUI = sender.enableUnsafeUI
					}
				}
			}
		}
	}
	
	private fun getTab(e: ActionEvent?): ConfigurationTabLabel? {
		if (e == null) {
			log.warn("Null ActionEvent")
			return null
		}
		val src = e.source
		if (src !is JMenuItem) {
			log.warn("Invalid source: {}", src)
			return null
		}
		val ctx = src.parent
		if (ctx !is JPopupMenu) {
			log.warn("Invalid source parent: {}", ctx)
			return null
		}
		val inv = ctx.invoker
		if (inv !is ConfigurationTabLabel) {
			log.warn("Invalid popup invoker: {}", inv)
			return null
		}
		return inv
	}
	
	internal val tabMouseListener = object : MouseAdapter() {
		private val contextMenu = JPopupMenu().apply {
			add(JMenuItem("Edit").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						proj.configurations.edit(indexOfTabComponent(tab))
					}
				}
			})
			add(JMenuItem("Rename").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a new name for the configuration:", "Rename", JOptionPane.QUESTION_MESSAGE)
						if (str != null && str.isNotEmpty()) {
							proj.configurations.rename(indexOfTabComponent(tab), str)
						}
					}
				}
			})
			add(JMenuItem("Duplicate").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a name for the duplicated configuration:", "Duplicate", JOptionPane.QUESTION_MESSAGE)
						if (str != null && str.isNotEmpty()) {
							proj.configurations.duplicate(indexOfTabComponent(tab), str)
						}
					}
				}
			})
			add(JMenuItem("Delete").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						proj.configurations.remove(indexOfTabComponent(tab))
					}
				}
			})
			addSeparator()
			add(JMenuItem("Move Left").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						val idx = indexOfTabComponent(tab)
						if (idx > 0) {
							proj.configurations.move(idx, idx - 1)
						}
					}
				}
			})
			add(JMenuItem("Move Right").apply {
				addActionListener { e ->
					val tab = getTab(e)
					if (tab != null) {
						val idx = indexOfTabComponent(tab)
						if (idx < tabCount - 2) {
							proj.configurations.move(idx, idx + 1)
						}
					}
				}
			})
		}
		
		override fun mouseReleased(e: MouseEvent?) {
			if (e == null) {
				log.warn("Null MouseEvent")
				return
			}
			if (e.isPopupTrigger) {
				contextMenu.show(e.component, e.x, e.y)
			}
		}
	}
	
	private fun getItem(e: ActionEvent?): RocketListItem? {
		if (e == null) {
			log.warn("Null ActionEvent")
			return null
		}
		val src = e.source
		if (src !is JMenuItem) {
			log.warn("Invalid source: {}", src)
			return null
		}
		val ctx = src.parent
		if (ctx !is JPopupMenu) {
			log.warn("Invalid source parent: {}", ctx)
			return null
		}
		val inv = ctx.invoker
		if (inv !is RocketListItem) {
			log.warn("Invalid popup invoker: {}", inv)
			return null
		}
		return inv
	}
	
	internal val componentMouseListener = object : MouseAdapter() {
		private val contextMenu = JPopupMenu().apply {
			add(JMenuItem("Delete").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e) ?: return
						proj.configurations.removeComponent(indexOfComponent(src.list), src.index)
					}
				})
			})
			addSeparator()
			add(JMenuItem("Move Up").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e) ?: return
						if (src.index > 0) {
							proj.configurations.moveComponent(indexOfComponent(src.list), src.index, src.index - 1)
						}
					}
				})
			})
			add(JMenuItem("Move Down").apply {
				addActionListener(object : ActionListener {
					override fun actionPerformed(e: ActionEvent?) {
						val src = getItem(e) ?: return
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
						log.warn("Null PopupMenuEvent")
						return null
					}
					val src = e.source
					if (src !is JPopupMenu) {
						log.warn("Invalid source: {}", src)
						return null
					}
					val item = src.invoker
					if (item !is RocketListItem) {
						log.warn("Invalid popup invoker: {}", item)
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
				log.warn("Null MouseEvent")
				return
			}
			if (e.isPopupTrigger) {
				contextMenu.show(e.component, e.x, e.y)
			}
		}
	}
	
	fun addConfig() {
		val str = JOptionPane.showInputDialog(this@ConfigurationTabView, "Please enter a name for the new configuration:", "Create", JOptionPane.QUESTION_MESSAGE)
		if (str != null && str.isNotEmpty()) {
			proj.configurations.add(str)
		}
	}
	
	fun addComponentToCurrent(component: File) {
		val idx = selectedIndex
		if (idx < tabCount - 1) {
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
				val tab = selectedIndex
				if (tab > 0 && tab == tabCount - 1) {
					selectedIndex = lastTab
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
				settingsListener.onSettingsUpdated(proj.app.settings)
				proj.app.settings.addListener(settingsListener)
			}

			override fun removeListeners() {
				proj.configurations.removeListener(configListener)
				proj.app.settings.removeListener(settingsListener)
			}
		})
	}
}
