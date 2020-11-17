package com.ffaero.openrocketassembler.model

import java.util.function.Consumer
import java.util.stream.Stream
import java.util.function.UnaryOperator
import java.util.Spliterator
import java.util.function.Predicate

internal abstract class ModelList<T> : MutableList<T> {
	private class SubList<T>(private var parent: ModelList<T>, private var from: Int, private var to: Int) : ModelList<T>() {
		override val size: Int
			get() = to - from

		override fun add(index: Int, element: T) {
			if (index <= size) {
				parent.add(from + index, element)
				++to
			}
		}
	
		override fun get(index: Int): T {
			if (index > size) {
				throw IndexOutOfBoundsException("Index is not within sublist")
			}
			return parent.get(from + index)
		}
		
		override fun equals(other: Any?): Boolean {
			if (other === null || !(other is SubList<*>)) {
				return false;
			}
			return parent === other.parent && from == other.from && to == other.to
		}
		
		override fun hashCode(): Int = 31 * (31 * parent.hashCode() + from) + to;
		
		override fun indexOf(element: T): Int {
			val it = parent.listIterator(from)
			for (i in 0 until size) {
				if (!it.hasNext()) {
					return -1
				}
				if (element == it.next()) {
					return i
				}
			}
			return -1
		}
		
		override fun lastIndexOf(element: T): Int {
			val it = parent.listIterator(to)
			for (i in size-1 downTo 0) {
				if (!it.hasPrevious()) {
					return -1
				}
				if (element == it.previous()) {
					return i
				}
			}
			return -1
		}
		
		override fun removeAt(index: Int): T {
			if (index > size) {
				throw IndexOutOfBoundsException("Index is not within sublist")
			}
			return parent.removeAt(from + index)
		}

		override fun listIterator(index: Int): MutableListIterator<T> = ListIterator<T>(parent, from + index, from, to)
		
		override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
			var from = from + fromIndex;
			if (from < this.from) {
				from = this.from;
			}
			var to = from + toIndex;
			if (to > this.to) {
				to = this.to
			}
			if (from >= to) {
				from = this.from
				to = this.to
			}
			return SubList<T>(parent, from, to)
		}
	}
	
	private class ListIterator<T>(private var parent: ModelList<T>, private var idx: Int, private val from: Int, private val to: Int) : MutableListIterator<T> {
		override fun add(element: T) = parent.add(idx, element)

		override fun equals(other: Any?): Boolean {
			if (!(other is ListIterator<*>)) {
				return false
			}
			return parent === other.parent && idx == other.idx && from == other.from && to == other.to
		}

		override fun hasNext(): Boolean = idx < to

		override fun hasPrevious(): Boolean = idx > from

		override fun hashCode(): Int = 31 * (31 * (31 * parent.hashCode() + idx) + from) + to

		override fun next(): T = parent.get(idx++)

		override fun nextIndex(): Int = idx - from

		override fun previous(): T = parent.get(--idx)

		override fun previousIndex(): Int = idx - 1 - from

		override fun remove() {
			parent.removeAt(idx)
		}

		override fun set(element: T) {
			parent.set(idx, element)
		}
	}
	
	override abstract val size: Int

	override abstract fun add(index: Int, element: T)

	override abstract fun get(index: Int): T
	
	override abstract fun equals(other: Any?): Boolean
	
	override abstract fun hashCode(): Int
	
	override abstract fun indexOf(element: T): Int
	
	override abstract fun lastIndexOf(element: T): Int
	
	override abstract fun removeAt(index: Int): T

	override fun add(element: T): Boolean {
		add(size, element)
		return true
	}

	override fun addAll(index: Int, elements: Collection<T>): Boolean {
		elements.forEachIndexed { i, e ->
			add(index + i, e)
		}
		return elements.size > 0
	}

	override fun addAll(elements: Collection<T>): Boolean {
		elements.forEach {
			add(size, it)
		}
		return elements.size > 0
	}

	override fun clear() {
		while (size > 0) {
			removeAt(0)
		}
	}

	override fun contains(element: T): Boolean {
		forEach {
			if (element == it) {
				return true
			}
		}
		return false
	}

	override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
	
	override fun isEmpty(): Boolean = size == 0

	override fun iterator(): MutableIterator<T> = listIterator(0)

	override fun listIterator(): MutableListIterator<T> = listIterator(0)

	override fun listIterator(index: Int): MutableListIterator<T> = ListIterator<T>(this, index, 0, size)
	
	override fun remove(element: T): Boolean {
		val idx = indexOf(element)
		if (idx >= 0) {
			removeAt(idx)
			return true
		}
		return false
	}

	override fun removeAll(elements: Collection<T>): Boolean {
		var removed = false
		elements.forEach {
			removed = remove(it) || removed
		}
		return removed
	}

	override fun retainAll(elements: Collection<T>): Boolean {
		var it = iterator()
		var removed = false
		while (it.hasNext()) {
			if (!elements.contains(it.next())) {
				it.remove()
				removed = true
			}
		}
		return removed
	}

	override fun set(index: Int, element: T): T {
		val v = removeAt(index)
		add(index, element)
		return v
	}

	override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
		var from = fromIndex;
		if (from < 0) {
			from = 0;
		}
		var to = toIndex;
		if (to > size) {
			to = size
		}
		if (from >= to) {
			from = 0
			to = 0
		}
		return SubList<T>(this, from, to)
	}
}
