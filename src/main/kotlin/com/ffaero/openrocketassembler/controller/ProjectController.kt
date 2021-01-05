package com.ffaero.openrocketassembler.controller

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project

class ProjectController(public val app: ApplicationController, private val model: Project.Builder, private var file: File?) : ControllerBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	private var stopped = false
	
	public fun stop() {
		if (stopped) {
			throw IllegalStateException("Cannot stop a project that has already been stopped")
		}
		stopped = true
		listener.onStop(this)
		app.removeProject(this)
	}
}
