package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.controller.*
import com.ffaero.openrocketassembler.view.menu.*
import org.slf4j.LoggerFactory
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.IOException
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter

class ApplicationView(internal val view: ViewManager, private val proj: ProjectController) {
	companion object {
		private val log = LoggerFactory.getLogger(ApplicationView::class.java)
	}

	private val frame = JFrame().apply {
		defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
		addWindowListener(object : WindowAdapter() {
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
					if (file.exists() && proj.app.settings.warnFileExists) {
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
				log.warn("Could not save file", ex)
				JOptionPane.showMessageDialog(frame, "Could not save file:\n" + ex.message, title, JOptionPane.ERROR_MESSAGE)
			}
		}
		return false
	}
	
	fun closeThen(title: String, action: String, func: Runnable) {
		if (proj.editingComponentTemplate && proj.app.settings.warnTemplateOpen) {
			if (JOptionPane.showConfirmDialog(frame, "Template is still open in OpenRocket.\nAll changes to it will be lost if you continue $action.\nContinue?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
				return
			}
		}
		if (proj.configurations.isEditingAny() && proj.app.settings.warnConfigOpen) {
			if (JOptionPane.showConfirmDialog(frame, "At least one configuration is still open in OpenRocket.\nAll changes to simulations and other data will be lost if you continue $action.\nContinue?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
				return
			}
		}
		if (proj.history.fileModified && proj.app.settings.warnUnsavedChanges) {
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
			add(HelpMenu())
			frame.jMenuBar = this
		}
		frame.apply {
			isVisible = true
			toFront()
			requestFocus()
			state = JFrame.NORMAL
			log.info("Window opened")
		}
		val historyListener = object : HistoryAdapter() {
			override fun onStatus(sender: HistoryController, modified: Boolean) = updateTitle(modified, proj.file)
		}.apply {
			onStatus(proj.history, proj.history.fileModified)
			proj.history.addListener(this)
		}
		val settingListener = object : SettingAdapter() {
			override fun onSettingsUpdated(sender: SettingController) {
				frame.preferredSize = sender.initialSize
				fileChooser.currentDirectory = sender.initialDir
			}
		}.apply {
			onSettingsUpdated(proj.app.settings)
			frame.size = frame.preferredSize
			proj.app.settings.addListener(this)
		}
		proj.addListener(object : ProjectAdapter() {
			override fun onStop(sender: ProjectController) {
				frame.dispose()
				proj.removeListener(this)
				proj.history.removeListener(historyListener)
				proj.app.settings.removeListener(settingListener)
			}

			override fun onFileChange(sender: ProjectController, file: File?) = updateTitle(proj.history.fileModified, file)
		}.apply {
			onFileChange(proj, proj.file)
		})
	}
}
