package com.ffaero.openrocketassembler.controller

class ApplicationListenerList : HashSet<ApplicationListener>(), ApplicationListener {
	override fun onProjectAdded(sender: ApplicationController, project: ProjectController) = forEach { it.onProjectAdded(sender, project) }
	override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) = forEach { it.onProjectRemoved(sender, project) }
}
