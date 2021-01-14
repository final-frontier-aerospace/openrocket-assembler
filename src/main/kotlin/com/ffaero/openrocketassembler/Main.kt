package com.ffaero.openrocketassembler

import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.ffaero.openrocketassembler.view.ViewManager
import java.io.File
import java.io.IOException

fun main(args: Array<String>) {
	val controller = ApplicationController()
	ViewManager(controller)
	var opened = false
	args.forEach {
		try {
			val file = File(it)
			if (file.exists()) {
				controller.addProject(Project.newBuilder(), file)
				opened = true
			}
		} catch (ex: IOException) {
		}
	}
	if (!opened && controller.settings.openFromCWD) {
		val files = File(".").listFiles { pathname -> pathname.extension == FileFormat.extension }
		if (files?.size == 1) {
			try {
				controller.addProject(Project.newBuilder(), files[0])
				opened = true
			} catch (ex: IOException) {
			}
		}
	}
	if (!opened) {
		controller.addProject(Project.newBuilder(), null)
	}
}
