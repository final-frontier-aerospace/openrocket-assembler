package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.proto.OpenRocketVersionOuterClass.OpenRocketVersion
import java.io.File

class OpenRocketController(private val app: ApplicationController) : DispatcherBase<OpenRocketListener, OpenRocketListenerList>(OpenRocketListenerList()) {
	val versions: List<String>
			get() = app.cache.openRocketVersionsList.map { it.name }
	
	internal fun fireUpdated() = listener.onOpenRocketVersionsUpdated(this, versions)
	
	fun checkForUpdates() = app.actions.openRocketUpdateCheck.checkNow(app)
	
	internal fun lookupVersion(version: String): OpenRocketVersion? = app.cache.openRocketVersionsList.find { it.name == version }

	internal fun jarForVersion(version: String): File? {
		return File(app.settings.cacheDir, lookupVersion(version)?.filename ?: return null)
	}

	fun launch(version: String, vararg args: String, death: Runnable? = null) {
		val jar = jarForVersion(version)
		if (jar == null) {
			death?.run()
			return
		}
		val proc = Runtime.getRuntime().exec(arrayOf(app.settings.javaPath, "-jar", jar.absolutePath).plus(args))
		if (death != null) {
			Thread {
				proc.waitFor()
				death.run()
			}.start()
		}
	}
}
