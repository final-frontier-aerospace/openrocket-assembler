package com.ffaero.openrocketassembler.view

import java.awt.event.MouseAdapter
import com.ffaero.openrocketassembler.controller.ComponentController
import com.ffaero.openrocketassembler.controller.ComponentAdapter
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.JFileChooser
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import javax.swing.event.PopupMenuListener
import javax.swing.event.PopupMenuEvent
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.awt.EventQueue

class ComponentList(private val view: EditorPanel, private val comp: ComponentController) : ListView<ComponentListItem, File>() {
	private val compListener = object : ComponentAdapter() {
		override fun onComponentsReset(sender: ComponentController, components: List<File>) {
			doReset(components)
			EventQueue.invokeLater {
				components.forEachIndexed { idx, it ->
					if (!it.exists()) {
						var message = "File referenced in project was not found:\n" + it.getPath()
						var type = JOptionPane.QUESTION_MESSAGE
						var fixed = false
						while (!fixed) {
							when (JOptionPane.showOptionDialog(this@ComponentList, message, "Open", JOptionPane.DEFAULT_OPTION, type, null, arrayOf("Close Project", "Ignore", "Browse for File"), null)) {
								0 -> { // Close project
									view.app.closeThen("Open", "closing project") {
										comp.proj.reset()
										fixed = true
									}
								}
								2 -> { // Browse for File
									if (fileChooser.showOpenDialog(this@ComponentList) == JFileChooser.APPROVE_OPTION) {
										val file = fileChooser.getSelectedFile()
										if (file != null && file.exists()) {
											if (comp.components.any { file.getAbsolutePath().equals(it.getAbsolutePath()) }) {
												message = "File is already in project:\n" + file.getPath()
												type = JOptionPane.ERROR_MESSAGE
											} else {
												comp.change(idx, file)
												fixed = true
											}
										}
									}
								}
								else -> { // Ignore
									fixed = true
								}
							}
						}
					}
				}
			}
		}
		
		override fun onComponentAdded(sender: ComponentController, index: Int, file: File) = doAdd(index, file)
		override fun onComponentRemoved(sender: ComponentController, index: Int) = doRemove(index)
		override fun onComponentMoved(sender: ComponentController, fromIndex: Int, toIndex: Int) = doMove(fromIndex, toIndex)
		override fun onComponentChanged(sender: ComponentController, index: Int, file: File) = doChange(index, file)
	}
	
	private val fileChooser = JFileChooser().apply {
		setCurrentDirectory(File("."))
		setFileFilter(FileNameExtensionFilter("OpenRocket File (*.ork)", "ork"))
	}
	
	private fun getItem(e: ActionEvent?): ComponentListItem? {
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
		if (!(inv is ComponentListView)) {
			return null
		}
		val parent = inv.getParent()
		if (!(parent is ComponentListItem)) {
			return null
		}
		return parent
	}
	
	private val contextHoverer = object : PopupMenuListener {
		private fun item(e: PopupMenuEvent?): ComponentListView? {
			if (e == null) {
				return null
			}
			val src = e.getSource()
			if (!(src is JPopupMenu)) {
				return null
			}
			val item = src.getInvoker()
			if (!(item is ComponentListView)) {
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
	}
	
	private val contextMenu = JPopupMenu().apply {
		add(JMenuItem("Edit").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null) {
						comp.proj.app.openrocket.launch(comp.proj.openRocketVersion, item.file!!.getAbsolutePath())
					}
				}
			})
		})
		add(JMenuItem("Relocate").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null) {
						if (JOptionPane.showConfirmDialog(this@ComponentList, "Are you sure you want to relocate this component?\nThis involves selecting a different file to replace all instances of this component in the project with.", "Relocate", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
							if (fileChooser.showOpenDialog(this@ComponentList) == JFileChooser.APPROVE_OPTION) {
								val file = fileChooser.getSelectedFile()
								if (file != null && file.exists()) {
									if (comp.components.any { file.getAbsolutePath().equals(it.getAbsolutePath()) }) {
										JOptionPane.showMessageDialog(this@ComponentList, "File is already in project:\n" + file.getPath(), "Relocate", JOptionPane.ERROR_MESSAGE)
									} else {
										comp.change(item.index, file)
									}
								}
							}
						}
					}
				}
			})
		})
		add(JMenuItem("Remove").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null) {
						comp.remove(item.index)
					}
				}
			})
		})
		addSeparator()
		add(JMenuItem("Append to Rocket").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null) {
						val file = item.file
						if (file != null) {
							view.configView.addComponentToCurrent(file)
						}
					}
				}
			})
		})
		add(JMenuItem("Move Up").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null && item.index > 0) {
						comp.move(item.index, item.index - 1)
					}
				}
			})
		})
		add(JMenuItem("Move Down").apply {
			addActionListener(object : ActionListener {
				override fun actionPerformed(e: ActionEvent?) {
					val item = getItem(e)
					if (item != null && item.index < comp.components.size - 1) {
						comp.move(item.index, item.index + 1)
					}
				}
			})
		})
		addPopupMenuListener(contextHoverer)
	}
	
	override fun create(): ComponentListItem = ComponentListItem().apply {
		addMouseListener(object : MouseAdapter() {
			override fun mouseReleased(e: MouseEvent?) {
				if (e == null) {
					return
				}
				if (e.isPopupTrigger()) {
					contextMenu.show(e.getComponent(), e.getX(), e.getY())
				}
			}
		})
	}

	override fun set(item: ComponentListItem, v: File) {
		item.file = v
	}

	init {
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				compListener.onComponentsReset(comp, comp.components)
				comp.addListener(compListener)
			}
			
			override fun removeListeners() {
				comp.removeListener(compListener)
			}
		})
		prefix = arrayOf(
			ComponentListView(true).apply {
				text = "template.ork"
				val ctx = JPopupMenu().apply {
					add(JMenuItem("Edit").apply {
						addActionListener(object : ActionListener {
							override fun actionPerformed(e: ActionEvent?) {
								if (comp.proj.editingComponentTemplate) {
									JOptionPane.showMessageDialog(this@ComponentList, "Template is already open in OpenRocket", "Edit", JOptionPane.ERROR_MESSAGE)
								} else {
									comp.proj.editComponentTemplate()
								}
							}
						})
					})
					addPopupMenuListener(contextHoverer)
				}
				addMouseListener(object : MouseAdapter() {
					override fun mouseReleased(e: MouseEvent?) {
						if (e == null) {
							return
						}
						if (e.isPopupTrigger()) {
							ctx.show(e.getComponent(), e.getX(), e.getY())
						}
					}
				})
			}
		)
		suffix = arrayOf(
			ComponentListView(true).apply {
				text = "Create new component..."
				addMouseListener(object : MouseAdapter() {
					override fun mouseClicked(e: MouseEvent?) {
						if (e == null) {
							return
						}
						if (e.getButton() == MouseEvent.BUTTON1 && fileChooser.showSaveDialog(this@ComponentList) == JFileChooser.APPROVE_OPTION) {
							var file = fileChooser.getSelectedFile()
							if (file != null) {
								if (!file.getName().contains('.')) {
									file = File(file.getPath() + ".ork")
								}
								if (comp.components.any { file.getAbsolutePath().equals(it.getAbsolutePath()) }) {
									JOptionPane.showMessageDialog(this@ComponentList, "File is already in project:\n" + file.getPath(), "Import", JOptionPane.ERROR_MESSAGE)
									return
								}
								if (file.exists()) {
									if (JOptionPane.showConfirmDialog(this@ComponentList, "File already exists.  Overwrite?", "Create", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
										return
									}
								}
								comp.create(file)
							}
						}
					}
				})
			},
			ComponentListView(true).apply {
				text = "Import existing component..."
				addMouseListener(object : MouseAdapter() {
					override fun mouseClicked(e: MouseEvent?) {
						if (e == null) {
							return
						}
						if (e.getButton() == MouseEvent.BUTTON1 && fileChooser.showOpenDialog(this@ComponentList) == JFileChooser.APPROVE_OPTION) {
							val file = fileChooser.getSelectedFile()
							if (file != null && file.exists()) {
								if (comp.components.any { file.getAbsolutePath().equals(it.getAbsolutePath()) }) {
									JOptionPane.showMessageDialog(this@ComponentList, "File is already in project:\n" + file.getPath(), "Import", JOptionPane.ERROR_MESSAGE)
								} else {
									comp.add(file)
								}
							}
						}
					}
				})
			}
		)
	}
}
