package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.FileSystem
import java.net.URL
import java.io.FileOutputStream
import java.io.IOException
import org.apache.commons.io.IOUtils
import java.io.File

class OpenRocketDownloader : ActionBase<ProjectController>() {
	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = enqueueAction(sender, 0)
	}
	
	override fun runAction(controller: ProjectController) {
		val ver = controller.app.openrocket.lookupVersion(controller.openRocketVersion)
		if (ver != null) {
			val file = FileSystem.getCacheFile(ver.getFilename())
			if (!file.exists()) {
				ActionReport(controller.app, "Downloading " + ver.getName() + "...").use {
					val tmpFile = File(file.getParentFile(), "~" + file.getName())
					try {
						URL(ver.getDownloadURL()).openStream().use { i ->
							FileOutputStream(tmpFile).use { o ->
								IOUtils.copy(i, o)
							}
						}
						tmpFile.renameTo(file)
					} catch (ex: IOException) {
						ex.printStackTrace()
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
