package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.Application
import javax.swing.event.ListDataListener
import javax.swing.ListModel

class RecentController internal constructor(private val app: Application) : ListModel<String> {
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
