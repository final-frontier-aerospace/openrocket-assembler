package com.ffaero.openrocketassembler.view

import java.awt.Dimension
import java.awt.Container
import java.awt.LayoutManager
import java.awt.Component

class ListViewLayoutManager<TItem : ListViewItem, TValue>(private val view: ListView<TItem, TValue>) : LayoutManager {
	private val components: Array<Component>
			get() = view.prefix.plus(view.items.filterIsInstance<Component>()).plus(view.suffix)
	
	override fun addLayoutComponent(name: String?, comp: Component?) = Unit
	override fun removeLayoutComponent(comp: Component?) = Unit
	
	private fun layoutComponent(width: Int, y: Int, comp: Component): Int {
		val pref = comp.getPreferredSize()
		val max = comp.getMaximumSize()
		val compWidth = minOf(max.width, width)
		comp.setSize(compWidth, pref.height)
		comp.setLocation((width - compWidth) / 2, y)
		return y + pref.height
	}

	override fun layoutContainer(parent: Container?) {
		if (parent == null) {
			return
		}
		val width = parent.getWidth()
		var y = 0
		components.forEach {
			val pref = it.getPreferredSize()
			val max = it.getMaximumSize()
			val compWidth = minOf(max.width, width)
			it.setSize(compWidth, pref.height)
			it.setLocation((width - compWidth) / 2, y)
			y += pref.height
		}
	}

	override fun minimumLayoutSize(parent: Container?): Dimension? {
		var w = 0
		var h = 0
		components.forEach {
			val min = it.getMinimumSize()
			w = maxOf(w, min.width)
			h += min.height
		}
		return Dimension(w, h)
	}

	override fun preferredLayoutSize(parent: Container?): Dimension? {
		var w = 0
		var h = 0
		components.forEach {
			val pref = it.getPreferredSize()
			w = maxOf(w, pref.width)
			h += pref.height
		}
		return Dimension(w, h)
	}
}
