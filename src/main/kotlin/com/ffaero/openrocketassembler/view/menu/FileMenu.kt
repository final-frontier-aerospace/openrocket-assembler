package com.ffaero.openrocketassembler.view.menu

import com.ffaero.openrocketassembler.controller.ApplicationController
import java.awt.event.ActionListener
import javax.swing.JMenuBar
import javax.swing.KeyStroke
import javax.swing.JMenu
import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import java.awt.event.KeyEvent

class FileMenu(app: ApplicationController, menuBar: JMenuBar) {
	private val newProjectMenu = JMenuItem("Project").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val newComponentMenu = JMenuItem("Component").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val newConfigurationMenu = JMenuItem("Configuration").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK or KeyEvent.ALT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val newMenu = JMenu("New").apply {
		add(newProjectMenu)
		add(newComponentMenu)
		add(newConfigurationMenu)
	}
	
	private val openMenu = JMenuItem("Open Project").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val saveMenu = JMenuItem("Save Project").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val saveAsMenu = JMenuItem("Save Project As").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val closeMenu = JMenuItem("Close Project").apply {
		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK))
		addActionListener(object : ActionListener {
			override fun actionPerformed(e: ActionEvent?) {
				TODO()
			}
		})
	}
	
	private val menu = JMenu("File").apply {
		setMnemonic('F')
		add(newMenu)
		add(openMenu)
		add(saveMenu)
		add(saveAsMenu)
		add(closeMenu)
		addSeparator()
		menuBar.add(this)
	}
}
