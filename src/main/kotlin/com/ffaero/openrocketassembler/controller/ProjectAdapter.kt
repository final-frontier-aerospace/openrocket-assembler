package com.ffaero.openrocketassembler.controller

open class ProjectAdapter : ProjectListener {
	override fun onStop(sender: ProjectController) = Unit
}
