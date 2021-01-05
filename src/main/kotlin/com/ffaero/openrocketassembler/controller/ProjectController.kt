package com.ffaero.openrocketassembler.controller

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.FileInputStream
import java.io.FileOutputStream

class ProjectController(public val app: ApplicationController, private val model: Project.Builder, private var file_: File?) : ControllerBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	private var stopped = false
	
	public var file: File?
			get() = file_
			set(value) {
				if (value != file_) {
					file_ = value
					listener.onFileChange(this, value)
				}
			}
	
	public fun stop() {
		if (stopped) {
			throw IllegalStateException("Cannot stop a project that has already been stopped")
		}
		stopped = true
		listener.onStop(this)
		app.removeProject(this)
	}
	
	public fun reset() {
		model.clear()
	}
	
	public fun load(file: File) {
		FileInputStream(file).use {
			model.clear()
			model.mergeFrom(it)
		}
	}
	
	public fun save(file: File) {
		FileOutputStream(file).use {
			model.build().writeTo(it)
		}
	}
}
