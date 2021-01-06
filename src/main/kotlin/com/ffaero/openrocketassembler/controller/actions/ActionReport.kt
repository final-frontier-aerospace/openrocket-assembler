package com.ffaero.openrocketassembler.controller.actions

import java.io.Closeable
import com.ffaero.openrocketassembler.controller.ApplicationController

class ActionReport(private val app: ApplicationController, private val status: String) : AutoCloseable, Closeable {
	override fun close() {
		app.backgroundStatus = ""
	}
	
	init {
		app.backgroundStatus = status
	}
}
