package com.ffaero.openrocketassembler.controller

interface ApplicationListener {
	fun onProjectAdded(sender: ApplicationController, project: ProjectController)
	fun onProjectRemoved(sender: ApplicationController, project: ProjectController)
}
