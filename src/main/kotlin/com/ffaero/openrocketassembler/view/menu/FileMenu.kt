package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.view.ViewManager
import com.ffaero.openrocketassembler.controller.ProjectController
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.KeyStroke
import java.awt.event.ActionEvent
import javax.swing.JFileChooser
import javax.swing.JMenuItem
import java.awt.Component
import java.awt.event.KeyEvent
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.FileNotFoundException
import java.io.IOException
import javax.swing.JOptionPane
import com.google.protobuf.InvalidProtocolBufferException
import java.io.File
import com.ffaero.openrocketassembler.FileFormat

class FileMenu(private val view: ViewManager, private val parent: Component, private val proj: ProjectController) : JMenu("File") {
	private val fileChooser = JFileChooser().apply {
		setCurrentDirectory(File("."))
		setFileFilter(FileNameExtensionFilter("OpenRocket Assembly File (*." + FileFormat.extension + ")", FileFormat.extension))
	}
	
	private val newMenu = JMenuItem("New").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = proj.reset()
		})
		this@FileMenu.add(this)
	}
	
	private val openMenu = JMenuItem("Open").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					val file = fileChooser.getSelectedFile()
					if (file != null) {
						try {
							proj.load(file)
							proj.file = file
							if (proj.lastSavedVersion < FileFormat.version) {
								if (JOptionPane.showConfirmDialog(parent, "Project was saved in an older version of OpenRocket Assembler.  Open anyways?", "Open", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
									proj.reset()
								}
							} else if (proj.lastSavedVersion > FileFormat.version) {
								if (JOptionPane.showConfirmDialog(parent, "Project was saved in a newer version of OpenRocket Assembler.  Open anyways?", "Open", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
									proj.reset()
								}
							}
						} catch (ex: FileNotFoundException) {
							JOptionPane.showMessageDialog(parent, "Could not find file:\n" + file.getPath(), "Open", JOptionPane.ERROR_MESSAGE)
						} catch (ex: InvalidProtocolBufferException) {
							JOptionPane.showMessageDialog(parent, "Could not read file:\nFile corruption detected:\n" + ex.message, "Open", JOptionPane.ERROR_MESSAGE)
						} catch (ex: IOException) {
							JOptionPane.showMessageDialog(parent, "Could not read file:\n" + ex.message, "Open", JOptionPane.ERROR_MESSAGE)
						}
					}
				}
			}
		})
		this@FileMenu.add(this)
	}
	
	private fun saveDialog(): File? {
		var file: File? = null
		if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile()
			if (file != null) {
				if (!file.getName().contains('.')) {
					file = File(file.getPath() + "." + FileFormat.extension)
				}
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(parent, "File already exists.  Overwrite?", "Save", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
						file = null
					}
				}
			}
		}
		return file
	}
	
	private fun doSave(file: File) {
		try {
			proj.save(file)
			proj.file = file
		} catch (ex: IOException) {
			JOptionPane.showMessageDialog(parent, "Could not save file:\n" + ex.message, "Save", JOptionPane.ERROR_MESSAGE)
		}
	}
	
	private val saveMenu = JMenuItem("Save").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				var file = proj.file
				if (file == null) {
					file = saveDialog()
				}
				if (file != null) {
					doSave(file)
				}
			}
		})
		this@FileMenu.add(this)
	}
	
	private val saveAsMenu = JMenuItem("Save As").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				val file = saveDialog()
				if (file != null) {
					doSave(file)
				}
			}
		})
		this@FileMenu.add(this)
		this@FileMenu.addSeparator()
	}
	
	private val exitMenu = JMenuItem("Exit").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.exit()
		})
		this@FileMenu.add(this)
	}
}
