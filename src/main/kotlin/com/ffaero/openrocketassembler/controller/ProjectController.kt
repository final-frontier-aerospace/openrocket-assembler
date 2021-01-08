package com.ffaero.openrocketassembler.controller

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.FileInputStream
import java.io.FileOutputStream
import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.controller.actions.DefaultOpenRocketVersion
import com.ffaero.openrocketassembler.controller.actions.OpenRocketDownloader
import com.ffaero.openrocketassembler.controller.actions.ActionRunner
import com.google.protobuf.ByteString
import com.ffaero.openrocketassembler.FileSystem

class ProjectController(public val app: ApplicationController, internal val model: Project.Builder, private var file_: File?) : DispatcherBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	private var stopped = false
	private var modified_ = false
	
	public val components = ComponentController(this)
	public val configurations = ConfigurationController(this)
	
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
				if (!modified_ && !ActionRunner.isRunner.get()) {
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
	
	public var componentTemplate: ByteString
			get() {
				val temp = model.getComponentTemplate()
				if (temp.isEmpty()) {
					return FileFormat.emptyORK
				} else {
					return temp
				}
			}
			set(value) {
				if (!model.getComponentTemplate().equals(value)) {
					model.setComponentTemplate(value)
					modified = true
				}
			}
	
	init {
		app.actions.addListeners(app.actions.projectActions, this)
	}
	
	public fun stop() {
		if (stopped) {
			throw IllegalStateException("Cannot stop a project that has already been stopped")
		}
		stopped = true
		listener.onStop(this)
		app.removeProject(this)
		app.actions.removeListeners(app.actions.projectActions, this)
	}
	
	private fun markUnmodified() {
		modified_ = false
		listener.onStatus(this, false)
	}
	
	private fun afterLoad(file: File?) {
		listener.onOpenRocketVersionChange(this, openRocketVersion)
		components.afterLoad(file)
		configurations.afterLoad()
		markUnmodified()
	}
	
	public fun reset() {
		file = null
		model.clear()
		afterLoad(null)
	}
	
	public fun load(file: File) {
		FileInputStream(file).use {
			model.clear()
			model.mergeFrom(it)
		}
		afterLoad(file)
	}
	
	public fun save(file: File) {
		FileOutputStream(file).use {
			model.setVersion(FileFormat.version)
			components.beforeSave(file, model)
			model.build().writeTo(it)
		}
		markUnmodified()
	}
	
	public fun makeID(): Int {
		val id = model.getNextID()
		model.setNextID(id + 1)
		return id
	}
	
	private var editingComponentTemplate_ = false
	public val editingComponentTemplate
			get() = editingComponentTemplate_
	
	public fun editComponentTemplate() {
		if (editingComponentTemplate_) {
			return
		}
		editingComponentTemplate_ = true
		val file = FileSystem.getTempFile(this, "template.ork")
		FileOutputStream(file).use {
			componentTemplate.writeTo(it)
		}
		app.openrocket.launch(openRocketVersion, file.getAbsolutePath()) {
			FileInputStream(file).use {
				componentTemplate = ByteString.readFrom(it)
			}
			file.delete()
			editingComponentTemplate_ = false
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
