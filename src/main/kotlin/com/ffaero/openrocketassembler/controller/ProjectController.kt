package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.controller.actions.ActionRunner
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.google.protobuf.ByteString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ProjectController(val app: ApplicationController, internal val model: Project.Builder, private var file_: File?) : DispatcherBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	private var stopped = false
	private var _modified = false
	
	val components = ComponentController(this)
	val configurations = ConfigurationController(this)
	
	var file: File?
			get() = file_
			set(value) {
				if (value != file_) {
					file_ = value
					listener.onFileChange(this, value)
				}
			}
	
	var modified: Boolean
			get() = _modified
			set(value) {
				if (!value) {
					throw IllegalArgumentException("Cannot set modified to false, instead save the file")
				}
				if (!_modified && !ActionRunner.isRunner.get()) {
					_modified = true
					listener.onStatus(this, true)
				}
			}
	
	val lastSavedVersion: Int
			get() = model.version
	
	var openRocketVersion: String
			get() = model.openRocketVersion ?: ""
			set(value) {
				if (model.openRocketVersion != value) {
					model.openRocketVersion = value
					listener.onOpenRocketVersionChange(this, value)
					modified = true
				}
			}
	
	var componentTemplate: ByteString
			get() {
				val temp = model.componentTemplate
				return if (temp.isEmpty) {
					FileFormat.emptyORK
				} else {
					temp
				}
			}
			set(value) {
				if (model.componentTemplate != value) {
					model.componentTemplate = value
					modified = true
				}
			}
	
	init {
		app.actions.addListeners(app.actions.projectActions, this)
	}
	
	fun stop() {
		if (stopped) {
			throw IllegalStateException("Cannot stop a project that has already been stopped")
		}
		stopped = true
		listener.onStop(this)
		app.removeProject(this)
		app.actions.removeListeners(app.actions.projectActions, this)
	}
	
	private fun markUnmodified() {
		_modified = false
		listener.onStatus(this, false)
	}
	
	private fun afterLoad(file: File?) {
		listener.onOpenRocketVersionChange(this, openRocketVersion)
		components.afterLoad(file)
		configurations.afterLoad()
		markUnmodified()
	}
	
	fun reset() {
		file = null
		model.clear()
		afterLoad(null)
	}
	
	fun load(file: File) {
		FileInputStream(file).use {
			model.clear()
			model.mergeFrom(it)
		}
		afterLoad(file)
	}
	
	fun save(file: File) {
		FileOutputStream(file).use {
			model.version = FileFormat.version
			components.beforeSave(file, model)
			model.build().writeTo(it)
		}
		markUnmodified()
	}
	
	fun makeID(): Int {
		val id = model.nextID
		model.nextID = id + 1
		return id
	}
	
	private var _editingComponentTemplate = false
	val editingComponentTemplate
			get() = _editingComponentTemplate
	
	fun editComponentTemplate() {
		if (_editingComponentTemplate) {
			return
		}
		_editingComponentTemplate = true
		val file = FileSystem.getTempFile(this, "template.ork")
		FileOutputStream(file).use {
			componentTemplate.writeTo(it)
		}
		app.openrocket.launch(openRocketVersion, file.absolutePath) {
			FileInputStream(file).use {
				componentTemplate = ByteString.readFrom(it)
			}
			file.delete()
			_editingComponentTemplate = false
		}
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
