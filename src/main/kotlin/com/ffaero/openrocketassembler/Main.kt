package com.ffaero.openrocketassembler

import com.ffaero.openrocketassembler.view.ViewManager
import com.ffaero.openrocketassembler.controller.ApplicationController
import java.io.IOException
import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.FilenameFilter
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.FileFilter

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
	if (!opened) {
		val files = File(".").listFiles(object : FileFilter {
			override fun accept(pathname: File) = pathname.extension.equals(FileFormat.extension)
		})
		if (files.size == 1) {
			try {
				controller.addProject(Project.newBuilder(), files[0])
				opened = true
			} catch (ex: IOException) {
			}
		}
		if (!opened) {
			controller.addProject(Project.newBuilder(), null)
		}
	}
}
