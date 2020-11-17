package com.ffaero.openrocketassembler

import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.model.Application
import com.ffaero.openrocketassembler.view.ApplicationView

@Suppress("UNUSED_VARIABLE")
fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
	val model = Application()
	val controller = ApplicationController(model)
	val view = ApplicationView(controller)
	controller.start()
}
