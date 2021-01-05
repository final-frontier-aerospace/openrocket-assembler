package com.ffaero.openrocketassembler.controller

import java.io.File

interface ProjectListener {
	fun onStop(sender: ProjectController)
	fun onStatus(sender: ProjectController, modified: Boolean)
	fun onFileChange(sender: ProjectController, file: File?)
	fun onOpenRocketVersionChange(sender: ProjectController, version: String)
}
