package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ApplicationController
import java.io.Closeable

class ActionReport(private val app: ApplicationController, private val status: String) : AutoCloseable, Closeable {
	override fun close() {
		app.backgroundStatus = ""
	}
	
	init {
		app.backgroundStatus = status
	}
}
