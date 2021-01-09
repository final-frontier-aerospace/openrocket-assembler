package com.ffaero.openrocketassembler.view

import java.awt.Component
import java.awt.Container
import java.awt.event.HierarchyEvent
import java.awt.event.HierarchyListener

abstract class ListenerLifecycleManager : HierarchyListener {
	private var oldParent: Container? = null
	
	protected abstract fun addListeners()
	protected abstract fun removeListeners()
	
	override fun hierarchyChanged(e: HierarchyEvent?) {
		if (e == null) {
			return
		}
		val src = e.source
		if (src !is Component) {
			return
		}
		val parent = src.parent
		if (oldParent == null) {
			if (parent != null) {
				addListeners()
			}
		} else if (parent == null) {
			removeListeners()
		}
		oldParent = parent
	}
}
