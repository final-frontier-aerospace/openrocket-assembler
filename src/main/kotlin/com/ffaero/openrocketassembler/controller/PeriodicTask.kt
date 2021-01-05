package com.ffaero.openrocketassembler.controller

class PeriodicTask(public val task: Runnable, private var lastRun_: Long, private var period_: Long) : DispatcherBase<PeriodicTaskListener, PeriodicTaskListenerList>(PeriodicTaskListenerList()) {
	public var lastRun: Long
			get() = lastRun_
			set(value) {
				if (lastRun_ != value) {
					lastRun_ = value
					listener.onRun(this)
				}
			}
	
	public var period: Long
			get() = period_
			set(value) {
				if (period_ != value) {
					period_ = value
					listener.onPeriodChange(this)
				}
			}
}
