package com.ffaero.openrocketassembler.controller

import java.util.function.Consumer

abstract class ListenerListBase<TListener> {
	private class TrackingSet<TListener>(copy: HashSet<TListener>) : HashSet<TListener>(copy) {
		public var forEaches = 0
	}
	
	private var set = TrackingSet(HashSet<TListener>())
	
	private fun cow(): HashSet<TListener> {
		if (set.forEaches > 0) {
			set = TrackingSet(set)
		}
		return set
	}
	
	public fun add(e: TListener) = cow().add(e)
	
	public fun remove(e: TListener) = cow().remove(e)
	
	public fun forEach(action: Consumer<TListener>) {
		val set = set
		++set.forEaches
		set.forEach(action)
		--set.forEaches
	}
}
