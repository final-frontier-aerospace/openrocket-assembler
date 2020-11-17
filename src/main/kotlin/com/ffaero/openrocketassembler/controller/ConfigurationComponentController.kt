package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.Application
import javax.swing.event.ListDataListener
import javax.swing.ListModel

class ConfigurationComponentController internal constructor(private val app: Application, private val configIndex: Int) : ListModel<String> {
	public fun add(componentListIndex: Int, insertIndex: Int) {
		TODO()
	}
	
	public fun remove(index: Int) {
		TODO()
	}
	
	public fun reorder(fromIndex: Int, toIndex: Int) {
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
