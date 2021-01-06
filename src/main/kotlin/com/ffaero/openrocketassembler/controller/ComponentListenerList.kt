package com.ffaero.openrocketassembler.controller

import java.io.File

class ComponentListenerList : ListenerListBase<ComponentListener>(), ComponentListener {
	override fun onComponentsReset(sender: ComponentController, components: List<File>) = forEach { it.onComponentsReset(sender, components) }
	override fun onComponentAdded(sender: ComponentController, index: Int, file: File) = forEach { it.onComponentAdded(sender, index, file) }
	override fun onComponentRemoved(sender: ComponentController, index: Int) = forEach { it.onComponentRemoved(sender, index) }
	override fun onComponentMoved(sender: ComponentController, fromIndex: Int, toIndex: Int) = forEach { it.onComponentMoved(sender, fromIndex, toIndex) }
	override fun onComponentChanged(sender: ComponentController, index: Int, file: File) = forEach { it.onComponentChanged(sender, index, file) }
}
