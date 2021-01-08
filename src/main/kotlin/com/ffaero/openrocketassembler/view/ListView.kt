package com.ffaero.openrocketassembler.view

import java.awt.Component
import javax.swing.JPanel

abstract class ListView<TItem : ListViewItem, TValue> : JPanel() {
	protected abstract fun create(): TItem
	protected abstract fun set(item: TItem, v: TValue)
	
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
		repaint()
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
		repaint()
	}
	
	protected fun doRemove(index: Int) {
		remove(items.get(index))
		items.removeAt(index)
		items.subList(index, items.size).forEach {
			--it.index
		}
		revalidate()
		repaint()
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
		repaint()
	}
	
	protected fun doChange(index: Int, v: TValue) {
		set(items.get(index), v)
	}
	
	init {
		setLayout(ListViewLayoutManager(this))
	}
}
