package com.ffaero.openrocketassembler.controller

class ProjectListenerList : HashSet<ProjectListener>(), ProjectListener {
	override fun onStop(sender: ProjectController) = forEach { it.onStop(sender) }
}
