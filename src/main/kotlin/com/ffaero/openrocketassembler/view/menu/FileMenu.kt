package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.view.ApplicationView
import com.google.protobuf.InvalidProtocolBufferException
import java.awt.event.KeyEvent
import java.io.FileNotFoundException
import java.io.IOException
import javax.swing.*

class FileMenu(private val view: ApplicationView, private val proj: ProjectController) : JMenu("File") {
	init {
		JMenuItem("New").apply {
			accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK)
			addActionListener {
				view.closeThen("New", "creating new project") {
					proj.reset()
				}
			}
			this@FileMenu.add(this)
		}

		JMenuItem("Open").apply {
			accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK)
			addActionListener {
				view.closeThen("Open", "opening other project") {
					if (view.fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
						val file = view.fileChooser.selectedFile
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
								JOptionPane.showMessageDialog(parent, "Could not find file:\n" + file.path, "Open", JOptionPane.ERROR_MESSAGE)
							} catch (ex: InvalidProtocolBufferException) {
								JOptionPane.showMessageDialog(parent, "Could not read file:\nFile corruption detected:\n" + ex.message, "Open", JOptionPane.ERROR_MESSAGE)
							} catch (ex: IOException) {
								JOptionPane.showMessageDialog(parent, "Could not read file:\n" + ex.message, "Open", JOptionPane.ERROR_MESSAGE)
							}
						}
					}
				}
			}
			this@FileMenu.add(this)
		}

		JMenuItem("Save").apply {
			accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
			addActionListener { view.saveProject(proj.file, "Save") }
			this@FileMenu.add(this)
		}

		JMenuItem("Save As").apply {
			accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK)
			addActionListener { view.saveProject(null, "Save As") }
			this@FileMenu.add(this)
			this@FileMenu.addSeparator()
		}

		JMenuItem("Exit").apply {
			addActionListener { view.view.exit() }
			this@FileMenu.add(this)
		}
	}
}
