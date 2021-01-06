package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileSystem
import java.io.FileOutputStream
import java.io.IOException
import java.io.FileInputStream
import com.ffaero.openrocketassembler.controller.actions.ActionFactory
import com.ffaero.openrocketassembler.model.proto.CacheOuterClass.Cache
import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project

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
	
	private var backgroundStatus_ = ""
	public var backgroundStatus: String
			get() = backgroundStatus_
			set(value) {
				backgroundStatus_ = value
				listener.onBackgroundStatus(this, value)
			}
	
	public var windowSplit: Float
			get() = cache.getWindowSplit()
			set(value) {
				if (value != cache.getWindowSplit()) {
					cache.setWindowSplit(value)
					writeCacheOnExit = true
					listener.onWindowSplitChanged(this, value)
				}
			}
	
	public val openrocket = OpenRocketController(this)
	
	init {
		actions.addListeners(actions.applicationActions, this)
	}
	
	public fun stop() {
		actions.removeListeners(actions.applicationActions, this)
		actions.stop()
		if (writeCacheOnExit) {
			writeCache()
		}
	}
	
	public fun addProject(model: Project.Builder, file: File?) = listener.onProjectAdded(this, ProjectController(this, model, file))
	internal fun removeProject(proj: ProjectController) = listener.onProjectRemoved(this, proj)
	
	public fun writeCache() {
		try {
			FileOutputStream(cacheFile).use {
				cache.build().writeTo(it)
				writeCacheOnExit = false
			}
		} catch (ex: IOException) {
		}
	}
}
