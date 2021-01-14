package com.ffaero.openrocketassembler.view

import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.Container
import java.awt.event.HierarchyEvent
import java.awt.event.HierarchyListener

abstract class ListenerLifecycleManager : HierarchyListener {
	companion object {
		private val log = LoggerFactory.getLogger(ListenerLifecycleManager::class.java)
	}

	private var oldParent: Container? = null
	
	protected abstract fun addListeners()
	protected abstract fun removeListeners()
	
	override fun hierarchyChanged(e: HierarchyEvent?) {
		if (e == null) {
			log.warn("Null HierarchyEvent")
			return
		}
		val src = e.source
		if (src !is Component) {
			log.warn("Invalid source: {}", src)
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
