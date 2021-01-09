package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.model.proto.OpenRocketVersionOuterClass.OpenRocketVersion
import java.io.File

class OpenRocketController(private val app: ApplicationController) : DispatcherBase<OpenRocketListener, OpenRocketListenerList>(OpenRocketListenerList()) {
	private val java: String
	
	init {
		val bin = File(File(System.getProperty("java.home")), "bin")
		var exe = File(bin, "javaw")
		if (exe.exists()) {
			exe = File(bin, "javaw.exe")
		}
		java = if (exe.exists()) {
			bin.absolutePath
		} else {
			"javaw"
		}
	}
	
	val versions: List<String>
			get() = app.cache.openRocketVersionsList.map { it.name }
	
	internal fun fireUpdated() = listener.onOpenRocketVersionsUpdated(this, versions)
	
	fun checkForUpdates() = app.actions.openRocketUpdateCheck.checkNow(app)
	
	internal fun lookupVersion(version: String): OpenRocketVersion? = app.cache.openRocketVersionsList.find { it.name == version }
	
	fun launch(version: String, vararg args: String, death: Runnable? = null) {
		val ver = lookupVersion(version)
		if (ver == null) {
			death?.run()
			return
		}
		val proc = Runtime.getRuntime().exec(arrayOf(java, "-jar", FileSystem.getCacheFile(ver.filename).absolutePath).plus(args))
		if (death != null) {
			Thread {
				proc.waitFor()
				death.run()
			}.start()
		}
	}
}
