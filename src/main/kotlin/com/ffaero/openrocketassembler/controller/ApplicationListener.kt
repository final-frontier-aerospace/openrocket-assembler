package com.ffaero.openrocketassembler.controller

interface ApplicationListener {
	fun onBackgroundStatus(sender: ApplicationController, status: String)
	fun onProjectAdded(sender: ApplicationController, project: ProjectController)
	fun onProjectRemoved(sender: ApplicationController, project: ProjectController)
	fun onWindowSplitChanged(sender: ApplicationController, split: Float)
}
