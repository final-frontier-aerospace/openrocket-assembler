package com.ffaero.openrocketassembler.controller

open class DispatcherBase<TListener, TListenerList : HashSet<TListener>>(protected val listener: TListenerList) {
	public fun addListener(listener: TListener) = this.listener.add(listener)
	public fun removeListener(listener: TListener) = this.listener.remove(listener)
}
