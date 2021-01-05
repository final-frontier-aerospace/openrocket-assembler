package com.ffaero.openrocketassembler.controller

open class ApplicationAdapter : ApplicationListener {
	override fun onProjectAdded(sender: ApplicationController, project: ProjectController) = Unit
	override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) = Unit
}
