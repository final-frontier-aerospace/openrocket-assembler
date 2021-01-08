package com.ffaero.openrocketassembler.view

import java.io.File
import javax.swing.JScrollPane

class RocketList(private val view: ConfigurationTabView) : JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
	private val list = object : ListView<RocketListItem, File>() {
		override fun create(): RocketListItem = RocketListItem(this@RocketList).apply {
			addMouseListener(view.componentMouseListener)
		}

		override fun set(item: RocketListItem, v: File) {
			item.file = v
		}
		
		public fun callReset(components: List<File>) = doReset(components)
		public fun callAdd(index: Int, component: File) = doAdd(index, component)
		public fun callRemove(index: Int) = doRemove(index)
		public fun callMove(fromIndex: Int, toIndex: Int) = doMove(fromIndex, toIndex)
		public fun callChange(index: Int, component: File) = doChange(index, component)
	}.apply {
		setViewportView(this)
	}
	
	public fun onReset(components: List<File>) = list.callReset(components)
	public fun onAdd(index: Int, component: File) = list.callAdd(index, component)
	public fun onRemove(index: Int) = list.callRemove(index)
	public fun onMove(fromIndex: Int, toIndex: Int) = list.callMove(fromIndex, toIndex)
	public fun onChange(index: Int, component: File) = list.callChange(index, component)
}
