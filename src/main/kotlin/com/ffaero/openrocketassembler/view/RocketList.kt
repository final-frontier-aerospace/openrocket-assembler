package com.ffaero.openrocketassembler.view

import java.io.File
import javax.swing.JScrollPane

class RocketList(private val view: ConfigurationTabView) : JScrollPane(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER) {
	private val list = object : ListView<RocketListItem, File>() {
		override fun create(): RocketListItem = RocketListItem(this@RocketList).apply {
			addMouseListener(view.componentMouseListener)
		}

		override fun set(item: RocketListItem, v: File) {
			item.file = v
		}
		
		fun callReset(components: List<File>) = doReset(components)
		fun callAdd(index: Int, component: File) = doAdd(index, component)
		fun callRemove(index: Int) = doRemove(index)
		fun callMove(fromIndex: Int, toIndex: Int) = doMove(fromIndex, toIndex)
		fun callChange(index: Int, component: File) = doChange(index, component)
	}.apply {
		setViewportView(this)
	}
	
	fun onReset(components: List<File>) = list.callReset(components)
	fun onAdd(index: Int, component: File) = list.callAdd(index, component)
	fun onRemove(index: Int) = list.callRemove(index)
	fun onMove(fromIndex: Int, toIndex: Int) = list.callMove(fromIndex, toIndex)
	fun onChange(index: Int, component: File) = list.callChange(index, component)
}
