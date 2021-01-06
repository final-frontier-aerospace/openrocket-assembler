package com.ffaero.openrocketassembler.view

import java.awt.BorderLayout
import java.awt.event.MouseListener
import java.io.File

class ComponentListItem : ListViewItem() {
	private val view = ComponentListView(false)
	
	private var file_: File? = null
	public var file: File?
		get() = file_
		set(value) {
			file_ = value
			view.text = value?.getName() ?: ""
		}
	
	override fun addMouseListener(l: MouseListener?) = view.addMouseListener(l)
	
	init {
		setLayout(BorderLayout())
		add(view, BorderLayout.CENTER)
	}
}
