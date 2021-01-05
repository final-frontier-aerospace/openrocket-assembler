package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.File

class ApplicationController : ControllerBase<ApplicationListener, ApplicationListenerList>(ApplicationListenerList()) {
	public fun addProject(model: Project.Builder, file: File?) = listener.onProjectAdded(this, ProjectController(this, model, file))
	internal fun removeProject(proj: ProjectController) = listener.onProjectRemoved(this, proj)
}
