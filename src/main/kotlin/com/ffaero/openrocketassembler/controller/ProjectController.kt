package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.model.HistoryTransaction
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import com.google.protobuf.ByteString
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ProjectController(val app: ApplicationController, internal val model: Project.Builder, private var file_: File?) : DispatcherBase<ProjectListener, ProjectListenerList>(ProjectListenerList()) {
	companion object {
		private val log = LoggerFactory.getLogger(ProjectController::class.java)
	}

	private var stopped = false

	internal val history = HistoryController(app)
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

	val lastSavedVersion: Int
			get() = model.version
	
	var openRocketVersion: String
			get() {
				val ver = model.openRocketVersion
				val versions = app.openrocket.versions
				return if ((ver.isNullOrEmpty() || !versions.contains(ver)) && versions.isNotEmpty()) {
					versions.first()
				} else {
					ver ?: ""
				}
			}
			set(value) {
				val old = model.openRocketVersion
				if (old != value) {
					history.perform(HistoryTransaction("Setting OpenRocket Version").add {
						model.openRocketVersion = value
						listener.onOpenRocketVersionChange(this, openRocketVersion)
					}.toUndo {
						model.openRocketVersion = old
						listener.onOpenRocketVersionChange(this, openRocketVersion)
					})
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
				val old = model.componentTemplate
				if (old != value) {
					history.perform(HistoryTransaction("Editing Component Template").add {
						model.componentTemplate = value
					}.toUndo {
						model.componentTemplate = old
					})
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
		val file = file
		if (file != null) {
			log.info("Closing project {}", file.absolutePath)
		} else {
			log.info("Closing new project")
		}
		listener.onStop(this)
		app.removeProject(this)
		app.actions.removeListeners(app.actions.projectActions, this)
	}

	private fun afterLoad(file: File?) {
		listener.onOpenRocketVersionChange(this, openRocketVersion)
		components.afterLoad(file)
		configurations.afterLoad()
		history.reset()
	}
	
	fun reset() {
		log.info("Resetting project")
		file = null
		model.clear()
		afterLoad(null)
	}
	
	fun load(file: File) {
		log.info("Loading project from {}", file.absolutePath)
		FileInputStream(file).use {
			model.clear()
			model.mergeFrom(it)
		}
		afterLoad(file)
	}
	
	fun save(file: File) {
		log.info("Saving project to {}", file.absolutePath)
		FileOutputStream(file).use {
			model.version = FileFormat.version
			components.beforeSave(file, model)
			model.build().writeTo(it)
		}
		history.afterSave()
	}
	
	fun makeID(): Int {
		val id = model.nextID
		model.nextID = id + 1
		return id
	}
	
	private var _editingComponentTemplate = false
	val editingComponentTemplate
			get() = _editingComponentTemplate

	private val templateFile: File
			get() = File(app.settings.tempDir, String.format("%08X-template.ork", hashCode()))

	internal val tempInUse: Set<File>
			get() = if (editingComponentTemplate) {
				configurations.tempInUse.plus(templateFile)
			} else {
				configurations.tempInUse
			}
	
	fun editComponentTemplate() {
		if (_editingComponentTemplate) {
			return
		}
		_editingComponentTemplate = true
		val file = templateFile
		FileOutputStream(file).use {
			componentTemplate.writeTo(it)
		}
		log.info("Launching {} to edit {}", openRocketVersion, file.absolutePath)
		app.openrocket.launch(openRocketVersion, file.absolutePath) {
			log.info("Processing changes to {}", file.absolutePath)
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
