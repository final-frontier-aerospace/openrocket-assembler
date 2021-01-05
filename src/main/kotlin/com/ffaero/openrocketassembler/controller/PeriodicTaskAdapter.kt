package com.ffaero.openrocketassembler.controller

open class PeriodicTaskAdapter : PeriodicTaskListener {
	override fun onRun(sender: PeriodicTask) = Unit
	override fun onPeriodChange(sender: PeriodicTask) = Unit
}
