package com.ffaero.openrocketassembler.controller

import java.io.File

class ProjectListenerList : HashSet<ProjectListener>(), ProjectListener {
	override fun onStop(sender: ProjectController) = forEach { it.onStop(sender) }
	override fun onFileChange(sender: ProjectController, file: File?) = forEach { it.onFileChange(sender, file) }
}
