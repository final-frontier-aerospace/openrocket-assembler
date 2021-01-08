package com.ffaero.openrocketassembler.view

import javax.swing.JLabel
import java.io.File
import javax.swing.BorderFactory
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class RocketListItem(public val list: RocketList) : ListViewItem() {
	private val label = JLabel()
	
	private var file_: File? = null
	public var file: File?
			get() = file_
			set(value) {
				file_ = value
				label.text = value?.getName() ?: ""
			}
	
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
