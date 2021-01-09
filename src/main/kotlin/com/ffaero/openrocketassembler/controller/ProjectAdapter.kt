package com.ffaero.openrocketassembler.controller

import java.io.File

open class ProjectAdapter : ProjectListener {
	override fun onStop(sender: ProjectController) = Unit
	override fun onFileChange(sender: ProjectController, file: File?) = Unit
	override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = Unit
}
