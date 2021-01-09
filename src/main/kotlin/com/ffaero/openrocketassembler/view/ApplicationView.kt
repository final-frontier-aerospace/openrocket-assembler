package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.controller.HistoryAdapter
import com.ffaero.openrocketassembler.controller.HistoryController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.view.menu.EditMenu
import com.ffaero.openrocketassembler.view.menu.FileMenu
import com.ffaero.openrocketassembler.view.menu.OpenRocketMenu
import com.ffaero.openrocketassembler.view.menu.WindowMenu
import java.awt.Dimension
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class ApplicationView(internal val view: ViewManager, private val proj: ProjectController) {
	private val frame = JFrame().apply {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
		preferredSize = Dimension(1024, 768)
		size = preferredSize
		addWindowListener(object : WindowListener {
			override fun windowActivated(e: WindowEvent?) = Unit
			override fun windowClosed(e: WindowEvent?) = Unit
			override fun windowDeactivated(e: WindowEvent?) = Unit
			override fun windowDeiconified(e: WindowEvent?) = Unit
			override fun windowIconified(e: WindowEvent?) = Unit
			override fun windowOpened(e: WindowEvent?) = Unit
			
			override fun windowClosing(e: WindowEvent?) = close()
		})
		contentPane.apply {
			val layout = SpringLayout()
			setLayout(layout)
			
			val statusBar = StatusBar(proj.app)
			add(statusBar)
			layout.putConstraint(SpringLayout.EAST, statusBar, 0, SpringLayout.EAST, this)
			layout.putConstraint(SpringLayout.SOUTH, statusBar, 0, SpringLayout.SOUTH, this)
			
			val editor = EditorPanel(this@ApplicationView, proj)
			add(editor)
			layout.putConstraint(SpringLayout.NORTH, editor, 0, SpringLayout.NORTH, this)
			layout.putConstraint(SpringLayout.EAST, editor, 0, SpringLayout.EAST, this)
			layout.putConstraint(SpringLayout.SOUTH, editor, 0, SpringLayout.SOUTH, this)
			layout.putConstraint(SpringLayout.WEST, editor, 0, SpringLayout.WEST, this)
		}
	}
	
	internal val fileChooser = JFileChooser().apply {
		currentDirectory = File(".")
		fileFilter = FileNameExtensionFilter("OpenRocket Assembly File (*." + FileFormat.extension + ")", FileFormat.extension)
	}

	fun saveProject(hint: File?, title: String): Boolean {
		var file = hint
		if (file == null) {
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.selectedFile
				if (file != null) {
					if (!file.name.contains('.')) {
						file = File(file.path + "." + FileFormat.extension)
					}
					if (file.exists()) {
						if (JOptionPane.showConfirmDialog(frame, "File already exists.  Overwrite?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
							file = null
						}
					}
				}
			}
		}
		if (file != null) {
			try {
				proj.save(file)
				proj.file = file
				return true
			} catch (ex: IOException) {
				JOptionPane.showMessageDialog(frame, "Could not save file:\n" + ex.message, title, JOptionPane.ERROR_MESSAGE)
			}
		}
		return false
	}
	
	fun closeThen(title: String, action: String, func: Runnable) {
		if (proj.editingComponentTemplate) {
			if (JOptionPane.showConfirmDialog(frame, "Template is still open in OpenRocket.\nAll changes to it will be lost if you continue $action.\nContinue?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
				return
			}
		}
		if (proj.configurations.isEditingAny()) {
			if (JOptionPane.showConfirmDialog(frame, "At least one configuration is still open in OpenRocket.\nAll changes to simulations and other data will be lost if you continue $action.\nContinue?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
				return
			}
		}
		if (proj.history.fileModified) {
			when (JOptionPane.showConfirmDialog(frame, "Project has unsaved changes.\nSave before $action?", title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				JOptionPane.YES_OPTION -> {
					if (!saveProject(proj.file, title)) {
						return
					}
				}
				JOptionPane.NO_OPTION -> {
				}
				else -> {
					return
				}
			}
		}
		func.run()
	}
	
	fun close() {
		closeThen("Close", "closing window") {
			proj.stop()
		}
	}
	
	private fun updateTitle(modified: Boolean, file: File?) {
		if (file == null) {
			if (modified) {
				frame.title = "OpenRocket Assembler *"
			} else {
				frame.title = "OpenRocket Assembler"
			}
		} else {
			if (modified) {
				frame.title = "OpenRocket Assembler - " + file.name + "*"
			} else {
				frame.title = "OpenRocket Assembler - " + file.name
			}
		}
	}
	
	init {
		JMenuBar().apply {
			add(FileMenu(this@ApplicationView, proj))
			add(EditMenu(proj))
			add(OpenRocketMenu(proj))
			add(WindowMenu(this@ApplicationView, proj))
			frame.jMenuBar = this
		}
		frame.apply {
			isVisible = true
			toFront()
			requestFocus()
			state = JFrame.NORMAL
		}
		val historyListener = object : HistoryAdapter() {
			override fun onStatus(sender: HistoryController, modified: Boolean) = updateTitle(modified, proj.file)
		}.apply {
			onStatus(proj.history, proj.history.fileModified)
			proj.history.addListener(this)
		}
		proj.addListener(object : ProjectAdapter() {
			override fun onStop(sender: ProjectController) {
				frame.dispose()
				proj.removeListener(this)
				proj.history.removeListener(historyListener)
			}

			override fun onFileChange(sender: ProjectController, file: File?) = updateTitle(proj.history.fileModified, file)
		}.apply {
			onFileChange(proj, proj.file)
		})
	}
}
