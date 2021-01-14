package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ApplicationController
import org.slf4j.LoggerFactory
import java.io.Closeable

class ActionReport(private val app: ApplicationController, private val status: String) : AutoCloseable, Closeable {
	companion object {
		private val log = LoggerFactory.getLogger(ActionReport::class.java)
	}

	override fun close() {
		log.info("Done {}", status)
		app.backgroundStatus = ""
	}
	
	init {
		log.info("Starting {}", status)
		app.backgroundStatus = status
	}
}
