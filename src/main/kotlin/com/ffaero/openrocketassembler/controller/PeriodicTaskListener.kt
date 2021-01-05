package com.ffaero.openrocketassembler.controller

interface PeriodicTaskListener {
	fun onRun(sender: PeriodicTask)
	fun onPeriodChange(sender: PeriodicTask)
}
