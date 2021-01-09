package com.ffaero.openrocketassembler.controller

open class DispatcherBase<TListener, TListenerList : ListenerListBase<TListener>>(protected val listener: TListenerList) {
	fun addListener(listener: TListener) = this.listener.add(listener)
	fun removeListener(listener: TListener) = this.listener.remove(listener)
}
