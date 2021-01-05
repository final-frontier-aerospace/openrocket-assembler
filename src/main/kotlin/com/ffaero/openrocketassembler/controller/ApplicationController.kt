package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileSystem
import java.io.FileOutputStream
import java.io.IOException
import java.io.FileInputStream
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
	internal val periodicRunner = PeriodicRunner()
	
	public val openrocket = OpenRocketController(this)
	
	public fun stop() = periodicRunner.stop()
	public fun addProject(model: Project.Builder, file: File?) = listener.onProjectAdded(this, ProjectController(this, model, file))
	internal fun removeProject(proj: ProjectController) = listener.onProjectRemoved(this, proj)
	
	public fun writeCache() {
		try {
			cacheFile.getParentFile().mkdirs()
			FileOutputStream(cacheFile).use {
				cache.build().writeTo(it)
			}
		} catch (ex: IOException) {
		}
	}
}
