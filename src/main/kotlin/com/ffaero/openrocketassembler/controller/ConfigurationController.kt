package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.Application
import javax.swing.event.ListDataListener
import javax.swing.ListModel

class ConfigurationController internal constructor(private val app: Application) : ListModel<String> {
	public fun create() {
		TODO()
	}
	
	public fun rename(index: Int, newName: String) {
		TODO()
	}
	
	public fun duplicate(index: Int) {
		TODO()
	}
	
	public fun open(index: Int) {
		TODO()
	}
	
	public fun getComponents(index: Int): ConfigurationComponentController = ConfigurationComponentController(app, index)
	
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
