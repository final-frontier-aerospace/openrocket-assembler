package com.ffaero.openrocketassembler.controller

import java.io.File

class ProjectListenerList : ListenerListBase<ProjectListener>(), ProjectListener {
	override fun onStop(sender: ProjectController) = forEach { it.onStop(sender) }
	override fun onFileChange(sender: ProjectController, file: File?) = forEach { it.onFileChange(sender, file) }
	override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = forEach { it.onOpenRocketVersionChange(sender, version) }
}
