package com.ffaero.openrocketassembler.controller

import java.io.File

interface ComponentListener {
	fun onComponentsReset(sender: ComponentController, components: List<File>)
	fun onComponentAdded(sender: ComponentController, index: Int, file: File)
	fun onComponentRemoved(sender: ComponentController, index: Int)
	fun onComponentMoved(sender: ComponentController, fromIndex: Int, toIndex: Int)
	fun onComponentChanged(sender: ComponentController, index: Int, file: File)
}
