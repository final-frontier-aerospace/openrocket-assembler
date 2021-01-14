package com.ffaero.openrocketassembler.view

import java.awt.Component
import java.awt.EventQueue
import javax.swing.JPanel

abstract class ListView<TItem : ListViewItem, TValue> : JPanel() {
	protected abstract fun create(): TItem
	protected abstract fun set(item: TItem, v: TValue)
	
	private var _prefix: Array<Component> = arrayOf()
	var prefix: Array<Component>
			get() = _prefix
			set(value) {
				_prefix.forEach {
					remove(it)
				}
				_prefix = value
				value.forEach {
					add(it)
				}
				revalidate()
			}
	
	private var _suffix: Array<Component> = arrayOf()
	var suffix: Array<Component>
			get() = _suffix
			set(value) {
				_suffix.forEach {
					remove(it)
				}
				_suffix = value
				value.forEach {
					add(it)
				}
				revalidate()
			}
	
	internal val items = ArrayList<TItem>()
	
	protected fun doReset(vals: List<TValue>) = EventQueue.invokeLater {
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
	
	protected fun doAdd(index: Int, v: TValue) = EventQueue.invokeLater {
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
	
	protected fun doRemove(index: Int) = EventQueue.invokeLater {
		remove(items[index])
		items.removeAt(index)
		items.subList(index, items.size).forEach {
			--it.index
		}
		revalidate()
		repaint()
	}
	
	protected fun doMove(fromIndex: Int, toIndex: Int) = EventQueue.invokeLater {
		val tmp = items[fromIndex]
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
	
	protected fun doChange(index: Int, v: TValue) = EventQueue.invokeLater {
		set(items[index], v)
	}
	
	init {
		layout = ListViewLayoutManager(this)
	}
}
