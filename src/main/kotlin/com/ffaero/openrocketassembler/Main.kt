package com.ffaero.openrocketassembler

import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.view.ApplicationView

@Suppress("UNUSED_VARIABLE")
fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
	val controller = ApplicationController()
	val view = ApplicationView(controller)
	controller.start()
}
