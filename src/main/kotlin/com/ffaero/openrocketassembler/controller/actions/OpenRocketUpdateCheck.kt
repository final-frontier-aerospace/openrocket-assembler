package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.controller.SettingAdapter
import com.ffaero.openrocketassembler.controller.SettingController
import com.ffaero.openrocketassembler.model.GitHubRelease
import com.ffaero.openrocketassembler.model.GitHubReleaseAsset
import com.ffaero.openrocketassembler.model.proto.OpenRocketVersionOuterClass.OpenRocketVersion
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*

class OpenRocketUpdateCheck : ActionBase<ApplicationController>() {
	companion object {
		private val gson = Gson()
	}

	private val settingsListener = object : SettingAdapter() {
		override fun onSettingsUpdated(sender: SettingController) {
			enqueueAction(sender.app, sender.app.cache.openRocketVersionsLastUpdate + sender.openrocketUpdatePeriod)
		}
	}
	
	override fun runAction(controller: ApplicationController) {
		ActionReport(controller, "Checking for OpenRocket updates...").use {
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
									if (c1 in '0'..'9') {
										return -1
									} else if (c2 in '0'..'9') {
										return 1
									}
								}
								return c2 - c1
							}
							wasDigit = c1 in '0'..'9'
							++i
						}
						return n2.length - n1.length
					}
				})
				controller.cache.clearOpenRocketVersions()
				list.forEach {
					controller.cache.addOpenRocketVersions(OpenRocketVersion.newBuilder().setName(it.first.name).setFilename(it.second.name).setDownloadURL(it.second.downloadURL).build())
				}
				val now = System.currentTimeMillis()
				controller.cache.openRocketVersionsLastUpdate = now
				controller.writeCache()
				controller.openrocket.fireUpdated()
				enqueueAction(controller, now + controller.settings.openrocketUpdatePeriod)
			}
		}
	}
	
	fun checkNow(controller: ApplicationController) = enqueueAction(controller, 0)

	override fun addListeners(controller: ApplicationController) {
		settingsListener.onSettingsUpdated(controller.settings)
		controller.settings.addListener(settingsListener)
	}

	override fun removeListeners(controller: ApplicationController) {
		controller.settings.removeListener(settingsListener)
		dequeueAction(controller)
	}
}
