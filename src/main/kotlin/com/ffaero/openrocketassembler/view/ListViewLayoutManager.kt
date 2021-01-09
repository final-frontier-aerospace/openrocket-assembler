package com.ffaero.openrocketassembler.view

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.LayoutManager

class ListViewLayoutManager<TItem : ListViewItem, TValue>(private val view: ListView<TItem, TValue>) : LayoutManager {
	private val components: Array<Component>
			get() = view.prefix.plus(view.items.toList()).plus(view.suffix)
	
	override fun addLayoutComponent(name: String?, comp: Component?) = Unit
	override fun removeLayoutComponent(comp: Component?) = Unit

	override fun layoutContainer(parent: Container?) {
		if (parent == null) {
			return
		}
		val width = parent.width
		var y = 0
		components.forEach {
			val pref = it.preferredSize
			val max = it.maximumSize
			val compWidth = minOf(max.width, width)
			it.setSize(compWidth, pref.height)
			it.setLocation((width - compWidth) / 2, y)
			y += pref.height
		}
	}

	override fun minimumLayoutSize(parent: Container?): Dimension {
		var w = 0
		var h = 0
		components.forEach {
			val min = it.minimumSize
			w = maxOf(w, min.width)
			h += min.height
		}
		return Dimension(w, h)
	}

	override fun preferredLayoutSize(parent: Container?): Dimension {
		var w = 0
		var h = 0
		components.forEach {
			val pref = it.preferredSize
			w = maxOf(w, pref.width)
			h += pref.height
		}
		return Dimension(w, h)
	}
}
