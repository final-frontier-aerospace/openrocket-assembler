package com.ffaero.openrocketassembler.controller

import com.google.gson.stream.JsonReader
import java.net.URL
import java.io.Reader
import java.io.InputStreamReader
import com.google.gson.Gson
import com.ffaero.openrocketassembler.model.GitHubRelease
import java.util.LinkedList
import com.ffaero.openrocketassembler.model.GitHubReleaseAsset
import java.util.Comparator
import com.ffaero.openrocketassembler.model.proto.OpenRocketVersionOuterClass.OpenRocketVersion
import java.lang.Runnable
import java.io.File
import com.ffaero.openrocketassembler.FileSystem

class OpenRocketController(private val app: ApplicationController) : DispatcherBase<OpenRocketListener, OpenRocketListenerList>(OpenRocketListenerList()) {
	private val java: String
	
	init {
		val bin = File(File(System.getProperty("java.home")), "bin")
		var exe = File(bin, "javaw")
		if (exe.exists()) {
			exe = File(bin, "javaw.exe")
		}
		if (exe.exists()) {
			java = bin.getAbsolutePath()
		} else {
			java = "javaw"
		}
	}
	
	public val versions: List<String>
			get() = app.cache.getOpenRocketVersionsList().map { it.getName() }
	
	internal fun fireUpdated() = listener.onOpenRocketVersionsUpdated(this, versions)
	
	public fun checkForUpdates() = app.actions.openRocketUpdateCheck.checkNow(app)
	
	internal fun lookupVersion(version: String): OpenRocketVersion? = app.cache.getOpenRocketVersionsList().find { it.getName().equals(version) }
	
	public fun launch(version: String, vararg args: String, death: Runnable? = null) {
		val ver = lookupVersion(version)
		if (ver == null) {
			death?.run()
			return
		}
		val proc = Runtime.getRuntime().exec(arrayOf(java, "-jar", FileSystem.getCacheFile(ver.getFilename()).getAbsolutePath()).plus(args))
		if (death != null) {
			Thread(object : Runnable {
				override fun run() {
					proc.waitFor()
					death.run()
				}
			}).start()
		}
	}
}
