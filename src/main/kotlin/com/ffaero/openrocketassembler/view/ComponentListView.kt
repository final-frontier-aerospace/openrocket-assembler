package com.ffaero.openrocketassembler.view

import java.awt.event.MouseAdapter
import javax.swing.JPanel
import javax.swing.JLabel
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import java.awt.Color
import javax.swing.BorderFactory
import java.awt.Font

class ComponentListView(special: Boolean) : JPanel() {
	private val label = JLabel().apply {
		if (special) {
			val font = getFont()
			setFont(Font(font.getName(), font.getStyle() or Font.ITALIC, font.getSize()))
		}
	}
	
	public var text: String
		get() = label.getText()
		set(value) = label.setText(value)
	
	private val defaultBackground = getBackground()
	private val hoverBackground = defaultBackground.darker()
	private var hoverCount = 0
	
	public fun hoverStart() {
		if (++hoverCount == 1) {
			setBackground(hoverBackground)
		}
	}
	
	public fun hoverEnd() {
		if (--hoverCount == 0) {
			setBackground(defaultBackground)
		}
	}
	
	init {
		setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20))
		setLayout(BorderLayout())
		add(label, BorderLayout.CENTER)
		addMouseListener(object : MouseAdapter() {
			override fun mouseEntered(e: MouseEvent?) = hoverStart()
			override fun mouseExited(e: MouseEvent?) = hoverEnd()
		})
	}
}
