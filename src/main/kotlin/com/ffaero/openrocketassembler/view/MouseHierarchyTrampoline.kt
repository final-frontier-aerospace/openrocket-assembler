package com.ffaero.openrocketassembler.view

import java.awt.AWTEvent
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.SwingUtilities

open class MouseHierarchyTrampoline : MouseListener {
	companion object {
		private val eventEnabled = Component::class.java.getDeclaredMethod("eventEnabled", AWTEvent::class.java)
	}

	private var enabled = false

	fun enable() {
		eventEnabled.isAccessible = true
		enabled = true
	}
	
	protected open fun proxyEvent(e: MouseEvent): Boolean = true
	
	private fun event(e: MouseEvent?) {
		if (!enabled) {
			return
		}
		val source = e?.source
		if (source == null || source !is Component || !proxyEvent(e)) {
			return
		}
		var ev = e
		var src: Component = source
		while (true) {
			val parent = src.parent ?: return
			ev = SwingUtilities.convertMouseEvent(src, ev, parent)
			if (eventEnabled.invoke(parent, ev) as Boolean) {
				parent.dispatchEvent(ev)
				return
			}
			src = parent
		}
	}
	
	override fun mouseClicked(e: MouseEvent?) = event(e)
	override fun mouseEntered(e: MouseEvent?) = event(e)
	override fun mouseExited(e: MouseEvent?) = event(e)
	override fun mousePressed(e: MouseEvent?) = event(e)
	override fun mouseReleased(e: MouseEvent?) = event(e)
}
