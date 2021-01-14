package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.controller.actions.ActionFactory
import com.ffaero.openrocketassembler.model.proto.CacheOuterClass.Cache
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ApplicationController : DispatcherBase<ApplicationListener, ApplicationListenerList>(ApplicationListenerList()) {
	companion object {
		private val log = LoggerFactory.getLogger(ApplicationController::class.java)
	}

	val logControl = LogController(this)

	val settings = SettingController(this).apply {
		load()
	}

	init {
		logControl.initSettings()
	}

	private val cacheFile
		get() = File(settings.cacheDir, "appcache.bin")

	internal val cache = Cache.newBuilder().apply {
		if (cacheFile.exists()) {
			try {
				FileInputStream(cacheFile).use {
					mergeFrom(it)
				}
				log.info("Loaded cache")
				log.info("Last OpenRocket update: {}", openRocketVersionsLastUpdate)
				log.info("Window split: {}", windowSplit)
			} catch (ex: IOException) {
				log.warn("Unable to read cache file", ex)
			}
		} else {
			log.info("Cache file not found")
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

	private val projects = HashSet<ProjectController>()

	val cacheInUse: Set<File>
			get() = projects.mapNotNull { openrocket.jarForVersion(it.openRocketVersion) }.toSet()

	val tempInUse: Set<File>
			get() = projects.flatMap { it.tempInUse }.plus(logControl.file).filterNotNull().toSet()

	private val settingListener = object : SettingAdapter() {
		override fun onSettingsUpdated(sender: SettingController) {
			writeCacheOnExit = true
		}
	}
	
	init {
		actions.addListeners(actions.applicationActions, this)
		settings.addListener(settingListener)
	}
	
	fun stop() {
		log.info("Application exiting")
		actions.removeListeners(actions.applicationActions, this)
		settings.removeListener(settingListener)
		actions.stop()
		if (writeCacheOnExit) {
			writeCache()
		}
		logControl.stop()
	}
	
	fun addProject(model: Project.Builder, file: File?) {
		val proj = ProjectController(this, model, file)
		projects.add(proj)
		listener.onProjectAdded(this, proj)
		if (file != null) {
			log.info("Opened project {}", file.absolutePath)
		} else {
			log.info("Created new project")
		}
	}

	internal fun removeProject(proj: ProjectController) {
		projects.remove(proj)
		listener.onProjectRemoved(this, proj)
		val file = proj.file
		if (file != null) {
			log.info("Closed project {}", file.absolutePath)
		} else {
			log.info("Closed new project")
		}
	}
	
	fun writeCache() {
		try {
			FileOutputStream(cacheFile).use {
				cache.build().writeTo(it)
				writeCacheOnExit = false
				log.info("Wrote cache file")
			}
		} catch (ex: IOException) {
			log.warn("Unable to write cache file", ex)
		}
	}

	init {
		log.info("Application initialized")
	}
}
