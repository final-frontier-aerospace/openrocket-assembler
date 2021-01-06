package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.FileSystem
import java.net.URL
import java.io.FileOutputStream
import java.io.IOException
import org.apache.commons.io.IOUtils
import java.io.File

class OpenRocketDownloader(private val proj: ProjectController) : IAction {
	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
			val ver = proj.app.openrocket.lookupVersion(version)
			if (ver != null) {
				val file = FileSystem.getCacheFile(ver.getFilename())
				if (!file.exists()) {
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
	
	override fun stop() {
		proj.removeListener(projectListener)
	}
	
	init {
		proj.addListener(projectListener)
	}
}
