package com.ffaero.openrocketassembler

import com.ffaero.openrocketassembler.view.ViewManager
import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project

@Suppress("UNUSED_VARIABLE")
fun main(@Suppress("UNUSED_PARAMETER") args: Array<String>) {
	val controller = ApplicationController()
	val view = ViewManager(controller)
	controller.addProject(Project.newBuilder(), null)
}
