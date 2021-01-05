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
import com.ffaero.openrocketassembler.view.ApplicationView

class FileMenu(private val view: ApplicationView, private val parent: Component, private val proj: ProjectController) : JMenu("File") {
	private val newMenu = JMenuItem("New").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.closeThen("New", "creating new project") {
				proj.reset()
			}
		})
		this@FileMenu.add(this)
	}
	
	private val openMenu = JMenuItem("Open").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.closeThen("Open", "opening other project") {
				if (view.fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					val file = view.fileChooser.getSelectedFile()
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
	
	private val saveMenu = JMenuItem("Save").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				view.saveProject(proj.file, "Save")
			}
		})
		this@FileMenu.add(this)
	}
	
	private val saveAsMenu = JMenuItem("Save As").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				view.saveProject(null, "Save As")
			}
		})
		this@FileMenu.add(this)
		this@FileMenu.addSeparator()
	}
	
	private val exitMenu = JMenuItem("Exit").apply {
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) = view.view.exit()
		})
		this@FileMenu.add(this)
	}
}
