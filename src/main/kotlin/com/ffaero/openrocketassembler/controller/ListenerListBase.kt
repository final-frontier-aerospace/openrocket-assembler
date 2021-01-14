package com.ffaero.openrocketassembler.controller

import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer

abstract class ListenerListBase<TListener> {
	private class TrackingSet<TListener>(copy: HashSet<TListener>) : HashSet<TListener>(copy) {
		var forEaches = 0
	}
	
	private val lock = ReentrantLock()
	
	private var set = TrackingSet(HashSet<TListener>())
	
	private fun cow(): HashSet<TListener> {
		lock.lock()
		try {
			if (set.forEaches > 0) {
				set = TrackingSet(set)
			}
			return set
		} finally {
			lock.unlock()
		}
	}
	
	fun add(e: TListener) = cow().add(e)
	
	fun remove(e: TListener) = cow().remove(e)

	fun isNotEmpty() = set.isNotEmpty()
	
	fun forEach(action: Consumer<TListener>) {
		lock.lock()
		try {
			val set = set
			++set.forEaches
			lock.unlock()
			try {
				set.forEach(action)
			} finally {
				lock.lock()
				--set.forEaches
			}
		} finally {
			lock.unlock()
		}
	}
}
