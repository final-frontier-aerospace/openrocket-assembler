package com.ffaero.openrocketassembler.controller

class ApplicationListenerList : HashSet<ApplicationListener>(), ApplicationListener {
	override fun onStart(sender: ApplicationController) = forEach { it.onStart(sender) }
	override fun onStop(sender: ApplicationController) = forEach { it.onStop(sender) }
}
