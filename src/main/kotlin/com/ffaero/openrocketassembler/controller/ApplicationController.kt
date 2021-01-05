package com.ffaero.openrocketassembler.controller

class ApplicationController : ControllerBase<ApplicationListener, ApplicationListenerList>(ApplicationListenerList()) {
	private var state = ApplicationState.Initializing
	
	public fun start() {
		if (state != ApplicationState.Initializing) {
			throw IllegalStateException("Cannot start controller multiple times")
		}
		state = ApplicationState.Running
		listener.onStart(this)
	}
	
	public fun stop() {
		if (state == ApplicationState.Exited) {
			throw IllegalStateException("Cannot stop controller multiple times")
		}
		state = ApplicationState.Exited
		listener.onStop(this)
	}
}
