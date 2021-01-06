package com.ffaero.openrocketassembler.view

import javax.swing.JFrame
import com.ffaero.openrocketassembler.view.menu.WindowMenu
import java.awt.event.WindowEvent
import java.awt.Dimension
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import javax.swing.JMenuBar
import java.awt.event.WindowListener
import com.ffaero.openrocketassembler.view.menu.FileMenu
import java.io.File
import javax.swing.UIManager
import com.ffaero.openrocketassembler.view.menu.OpenRocketMenu
import javax.swing.JOptionPane
import javax.swing.JFileChooser
import com.ffaero.openrocketassembler.FileFormat
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.IOException
import javax.swing.SpringLayout

class ApplicationView(internal val view: ViewManager, private val proj: ProjectController) {
	private val frame = JFrame().apply {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE)
		extendedState = JFrame.MAXIMIZED_BOTH
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
		getContentPane().apply {
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
		setCurrentDirectory(File("."))
		setFileFilter(FileNameExtensionFilter("OpenRocket Assembly File (*." + FileFormat.extension + ")", FileFormat.extension))
	}
	
	private val menu = JMenuBar().apply {
		add(FileMenu(this@ApplicationView, frame, proj))
		add(OpenRocketMenu(frame, proj))
		add(WindowMenu(this@ApplicationView, proj))
		frame.setJMenuBar(this)
	}
	
	public fun saveProject(hint: File?, title: String): Boolean {
		var file = hint
		if (file == null) {
			if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile()
				if (file != null) {
					if (!file.getName().contains('.')) {
						file = File(file.getPath() + "." + FileFormat.extension)
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
	
	public fun closeThen(title: String, action: String, func: Runnable) {
		if (proj.editingComponentTemplate) {
			if (JOptionPane.showConfirmDialog(frame, "Template is still open in OpenRocket.\nAll changes to it will be lost if you continue " + action + ".\nContinue?", title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
				return
			}
		}
		if (proj.modified) {
			when (JOptionPane.showConfirmDialog(frame, "Project has unsaved changes.\nSave before " + action + "?", title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
				JOptionPane.YES_OPTION -> {
					if (!saveProject(proj.file, title)) {
						return
					}
				}
				else -> {
					return
				}
			}
		}
		func.run()
	}
	
	public fun close() {
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
				frame.title = "OpenRocket Assembler - " + file.getName() + "*"
			} else {
				frame.title = "OpenRocket Assembler - " + file.getName()
			}
		}
	}
	
	init {
		frame.apply {
			setVisible(true)
			toFront()
			requestFocus()
			setState(JFrame.NORMAL)
		}
		proj.addListener(object : ProjectAdapter() {
			override fun onStop(sender: ProjectController) {
				frame.dispose()
				proj.removeListener(this)
			}
			
			override fun onStatus(sender: ProjectController, modified: Boolean) = updateTitle(modified, proj.file)
			override fun onFileChange(sender: ProjectController, file: File?) = updateTitle(proj.modified, file)
		}.apply {
			onFileChange(proj, proj.file)
		})
	}
}
