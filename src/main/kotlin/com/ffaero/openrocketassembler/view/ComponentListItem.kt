package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.model.TemplateFile
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JLabel

class ComponentListItem : ListViewItem() {
	private val label = JLabel()
	
	private var _file: File? = null
	var file: File?
		get() = _file
		set(value) {
			_file = value
			label.text = value?.name ?: ""
			if (value is TemplateFile) {
				val font = label.font
				label.font = Font(font.name, font.style or Font.ITALIC, font.size)
			}
		}
	
	private val defaultBackground = background
	private val hoverBackground = defaultBackground.darker()
	private var hoverCount = 0
	
	fun hoverStart() {
		if (++hoverCount == 1) {
			background = hoverBackground
		}
	}
	
	fun hoverEnd() {
		if (--hoverCount == 0) {
			background = defaultBackground
		}
	}
	
	init {
		border = BorderFactory.createEmptyBorder(10, 20, 10, 20)
		layout = BorderLayout()
		add(label, BorderLayout.CENTER)
		addMouseListener(object : MouseAdapter() {
			override fun mouseEntered(e: MouseEvent?) = hoverStart()
			override fun mouseExited(e: MouseEvent?) = hoverEnd()
		})
	}
}
