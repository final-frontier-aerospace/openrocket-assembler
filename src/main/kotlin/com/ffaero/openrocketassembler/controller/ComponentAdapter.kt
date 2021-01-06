package com.ffaero.openrocketassembler.controller

import java.io.File

open class ComponentAdapter : ComponentListener {
	override fun onComponentsReset(sender: ComponentController, components: List<File>) = Unit
	override fun onComponentAdded(sender: ComponentController, index: Int, file: File) = Unit
	override fun onComponentRemoved(sender: ComponentController, index: Int) = Unit
	override fun onComponentMoved(sender: ComponentController, fromIndex: Int, toIndex: Int) = Unit
	override fun onComponentChanged(sender: ComponentController, index: Int, file: File) = Unit
}
