package com.ffaero.openrocketassembler.controller

class PeriodicTaskListenerList : HashSet<PeriodicTaskListener>(), PeriodicTaskListener {
	override fun onRun(sender: PeriodicTask) = forEach { it.onRun(sender) }
	override fun onPeriodChange(sender: PeriodicTask) = forEach { it.onPeriodChange(sender) }
}
