package com.ffaero.openrocketassembler.controller

interface ApplicationListener {
	fun onStart(sender: ApplicationController)
	fun onStop(sender: ApplicationController)
}
