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
	
	private val task = PeriodicTask(object: Runnable {
		override fun run() = checkForUpdates()
	}, app.cache.getOpenRocketVersionsLastUpdate(), 1000 * 60 * 60 * 24 * 7).apply {
		app.periodicRunner.addTask(this)
	}
	
	public val versions: List<String>
			get() = app.cache.getOpenRocketVersionsList().map { it.getName() }
	
	public fun checkForUpdates() {
		val gson = Gson()
		val list = LinkedList<Pair<GitHubRelease, GitHubReleaseAsset>>()
		JsonReader(InputStreamReader(URL("https://api.github.com/repos/openrocket/openrocket/releases?per_page=100").openStream())).use {
			it.beginArray()
			while (it.hasNext()) {
				val rel = gson.fromJson<GitHubRelease>(it, GitHubRelease::class.java)
				if (rel.name == null || rel.assets == null) {
					continue
				}
				for (asset in rel.assets!!) {
					if (asset.name != null && asset.downloadURL != null && asset.contentType == "application/java-archive") {
						list.add(Pair(rel, asset))
						break
					}
				}
			}
			it.endArray()
		}
		if (!list.isEmpty()) {
			list.sortWith(object : Comparator<Pair<GitHubRelease, GitHubReleaseAsset>> {
				override fun compare(o1: Pair<GitHubRelease, GitHubReleaseAsset>, o2: Pair<GitHubRelease, GitHubReleaseAsset>): Int {
					var wasDigit = false
					val n1 = o1.first.name!!
					val n2 = o2.first.name!!
					var i = 0
					while (i < n1.length && i < n2.length) {
						val c1 = n1[i]
						val c2 = n2[i]
						if (c1 != c2) {
							if (wasDigit) {
								if (c1 >= '0' && c1 <= '9') {
									return -1
								} else if (c2 >= '0' && c2 <= '9') {
									return 1
								}
							}
							return c2 - c1
						}
						wasDigit = c1 >= '0' && c1 <= '9'
						++i
					}
					return n2.length - n1.length
				}
			})
			app.cache.clearOpenRocketVersions()
			list.forEach {
				app.cache.addOpenRocketVersions(OpenRocketVersion.newBuilder().setName(it.first.name).setFilename(it.second.name).setDownloadURL(it.second.downloadURL).build())
			}
			task.lastRun = System.currentTimeMillis()
			app.cache.setOpenRocketVersionsLastUpdate(task.lastRun)
			app.writeCache()
			listener.onOpenRocketVersionsUpdated(this, versions)
		}
	}
	
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
