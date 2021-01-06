package com.ffaero.openrocketassembler.view

import javax.swing.JPanel
import java.awt.Component

abstract class ListView<TItem : ListViewItem, TValue> : JPanel() {
	protected abstract fun create(): TItem
	protected abstract fun set(item: TItem, v: TValue)
	protected abstract fun addListeners()
	protected abstract fun removeListeners()
	
	private var prefix_: Array<Component> = arrayOf()
	public var prefix: Array<Component>
			get() = prefix_
			set(value) {
				prefix_.forEach {
					remove(it)
				}
				prefix_ = value
				value.forEach {
					add(it)
				}
				revalidate()
			}
	
	private var suffix_: Array<Component> = arrayOf()
	public var suffix: Array<Component>
			get() = suffix_
			set(value) {
				suffix_.forEach {
					remove(it)
				}
				suffix_ = value
				value.forEach {
					add(it)
				}
				revalidate()
			}
	
	internal val items = ArrayList<TItem>()
	
	protected fun doReset(vals: List<TValue>) {
		items.forEach {
			remove(it)
		}
		items.clear()
		vals.forEach {
			items.add(create().apply {
				index = items.size
				set(this, it)
				this@ListView.add(this)
			})
		}
		revalidate()
	}
	
	protected fun doAdd(index: Int, v: TValue) {
		items.add(index, create().apply {
			this.index = index
			set(this, v)
			this@ListView.add(this)
		})
		items.subList(index + 1, items.size).forEach {
			++it.index
		}
		revalidate()
	}
	
	protected fun doRemove(index: Int) {
		remove(items.get(index))
		items.removeAt(index)
		items.subList(index, items.size).forEach {
			--it.index
		}
		revalidate()
	}
	
	protected fun doMove(fromIndex: Int, toIndex: Int) {
		val tmp = items.get(fromIndex)
		items.removeAt(fromIndex)
		tmp.index = toIndex
		items.add(toIndex, tmp)
		if (fromIndex < toIndex) {
			items.subList(fromIndex, toIndex).forEach {
				--it.index
			}
		} else if (fromIndex > toIndex) {
			items.subList(toIndex + 1, fromIndex + 1).forEach {
				++it.index
			}
		}
		revalidate()
	}
	
	protected fun doChange(index: Int, v: TValue) {
		set(items.get(index), v)
	}
	
	init {
		setLayout(ListViewLayoutManager(this))
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() = this@ListView.addListeners()
			override fun removeListeners() = this@ListView.removeListeners()
		})
	}
}
