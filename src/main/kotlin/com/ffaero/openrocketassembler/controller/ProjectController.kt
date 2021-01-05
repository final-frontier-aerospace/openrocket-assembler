package com.ffaero.openrocketassembler.controller

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.FileInputStream
import java.io.FileOutputStream
import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.controller.actions.DefaultOpenRocketVersion
import com.ffaero.openrocketassembler.controller.actions.IAction

class ProjectController(public val app: ApplicationController, private val model: Project.Builder, private var file_: File?) : DispatcherBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	private var stopped = false
	private var modified_ = false
	
	public var file: File?
			get() = file_
			set(value) {
				if (value != file_) {
					file_ = value
					listener.onFileChange(this, value)
				}
			}
	
	public var modified: Boolean
			get() = modified_
			set(value) {
				if (!value) {
					throw IllegalArgumentException("Cannot set modified to false, instead save the file")
				}
				if (!modified_) {
					modified_ = true
					listener.onStatus(this, true)
				}
			}
	
	public val lastSavedVersion: Int
			get() = model.getVersion()
	
	public var openRocketVersion: String
			get() {
				val ver = model.getOpenRocketVersion()
				if (ver == null) {
					return ""
				}
				return ver
			}
			set(value) {
				if (model.getOpenRocketVersion() != value) {
					model.setOpenRocketVersion(value)
					listener.onOpenRocketVersionChange(this, value)
					modified = true
				}
			}
	
	private val actions = arrayOf(
		DefaultOpenRocketVersion(this)
	)
	
	public fun stop() {
		if (stopped) {
			throw IllegalStateException("Cannot stop a project that has already been stopped")
		}
		stopped = true
		listener.onStop(this)
		app.removeProject(this)
		actions.forEach {
			it.stop()
		}
	}
	
	private fun markUnmodified() {
		modified_ = false
		listener.onStatus(this, false)
	}
	
	private fun afterLoad() {
		listener.onOpenRocketVersionChange(this, openRocketVersion)
		markUnmodified()
	}
	
	public fun reset() {
		file = null
		model.clear()
		afterLoad()
	}
	
	public fun load(file: File) {
		FileInputStream(file).use {
			model.clear()
			model.mergeFrom(it)
		}
		afterLoad()
	}
	
	public fun save(file: File) {
		FileOutputStream(file).use {
			model.setVersion(FileFormat.version).build().writeTo(it)
		}
		markUnmodified()
	}
	
	init {
		val file = file
		if (file != null) {
			load(file)
		} else {
			reset()
		}
	}
}
