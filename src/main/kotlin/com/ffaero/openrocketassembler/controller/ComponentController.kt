package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.Application
import com.ffaero.openrocketassembler.model.Component
import javax.swing.event.ListDataListener
import javax.swing.ListModel
import java.io.File

class ComponentController internal constructor(private val app: Application) : ListModel<String> {
	public fun create(file: File) {
		TODO()
	}
	
	public fun import(file: File) {
		TODO()
	}
	
	public fun rename(index: Int, newName: String) {
		TODO()
	}
	
	public fun duplicate(index: Int, file: File) {
		TODO()
	}
	
	public fun open(index: Int) {
		TODO()
	}
	
	override fun addListDataListener(l: ListDataListener?) {
		TODO()
	}

	override fun getElementAt(index: Int): String? {
		TODO()
	}

	override fun getSize(): Int {
		TODO()
	}

	override fun removeListDataListener(l: ListDataListener?) {
		TODO()
	}
}
