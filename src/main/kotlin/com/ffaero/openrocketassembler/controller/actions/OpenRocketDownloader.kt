package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.ProjectController
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

class OpenRocketDownloader : ActionBase<ProjectController>() {
	companion object {
		private val log = LoggerFactory.getLogger(OpenRocketDownloader::class.java)
	}

	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = enqueueAction(sender, 0)
	}
	
	override fun runAction(controller: ProjectController) {
		val ver = controller.app.openrocket.lookupVersion(controller.openRocketVersion)
		if (ver != null) {
			val file = File(controller.app.settings.cacheDir, ver.filename)
			if (!file.exists()) {
				ActionReport(controller.app, "Downloading " + ver.name + "...").use {
					val tmpFile = File(file.parentFile, "~" + file.name)
					try {
						URL(ver.downloadURL).openStream().use { i ->
							FileOutputStream(tmpFile).use { o ->
								IOUtils.copy(i, o)
							}
						}
						tmpFile.renameTo(file)
					} catch (ex: IOException) {
						log.warn("Unable to download OpenRocket", ex)
					}
				}
			}
		}
	}

	override fun addListeners(controller: ProjectController) {
		controller.addListener(projectListener)
	}

	override fun removeListeners(controller: ProjectController) {
		controller.removeListener(projectListener)
	}
}
