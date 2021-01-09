package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.controller.actions.ActionFactory
import com.ffaero.openrocketassembler.model.proto.CacheOuterClass.Cache
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ApplicationController : DispatcherBase<ApplicationListener, ApplicationListenerList>(ApplicationListenerList()) {
	private val cacheFile = FileSystem.getCacheFile("appcache.bin")
	internal val cache = Cache.newBuilder().apply {
		if (cacheFile.exists()) {
			try {
				FileInputStream(cacheFile).use {
					mergeFrom(it)
				}
			} catch (ex: IOException) {
			}
		}
	}
	internal val actions = ActionFactory()
	private var writeCacheOnExit = false
	
	private var _backgroundStatus = ""
	var backgroundStatus: String
			get() = _backgroundStatus
			set(value) {
				_backgroundStatus = value
				listener.onBackgroundStatus(this, value)
			}
	
	var windowSplit: Float
			get() = cache.windowSplit
			set(value) {
				if (value != cache.windowSplit) {
					cache.windowSplit = value
					writeCacheOnExit = true
					listener.onWindowSplitChanged(this, value)
				}
			}
	
	val openrocket = OpenRocketController(this)
	
	init {
		actions.addListeners(actions.applicationActions, this)
	}
	
	fun stop() {
		actions.removeListeners(actions.applicationActions, this)
		actions.stop()
		if (writeCacheOnExit) {
			writeCache()
		}
	}
	
	fun addProject(model: Project.Builder, file: File?) = listener.onProjectAdded(this, ProjectController(this, model, file))
	internal fun removeProject(proj: ProjectController) = listener.onProjectRemoved(this, proj)
	
	fun writeCache() {
		try {
			FileOutputStream(cacheFile).use {
				cache.build().writeTo(it)
				writeCacheOnExit = false
			}
		} catch (ex: IOException) {
		}
	}
}
