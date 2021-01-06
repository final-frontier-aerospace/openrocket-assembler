package com.ffaero.openrocketassembler.controller

open class ApplicationAdapter : ApplicationListener {
	override fun onBackgroundStatus(sender: ApplicationController, status: String) = Unit
	override fun onProjectAdded(sender: ApplicationController, project: ProjectController) = Unit
	override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) = Unit
	override fun onWindowSplitChanged(sender: ApplicationController, split: Float) = Unit
}
