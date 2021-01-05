package com.ffaero.openrocketassembler.controller

import java.io.File

interface ProjectListener {
	fun onStop(sender: ProjectController)
	fun onFileChange(sender: ProjectController, file: File?)
}
