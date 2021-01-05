package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.OpenRocketController
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.OpenRocketAdapter

class DefaultOpenRocketVersion(private val proj: ProjectController) : IAction {
	private val openRocketListener = object : OpenRocketAdapter() {
		override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) {
			if (!versions.isEmpty() && proj.openRocketVersion.isEmpty()) {
				proj.openRocketVersion = versions.first()
			}
		}
	}
	
	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) {
			if (version.isEmpty() && !proj.app.openrocket.versions.isEmpty()) {
				proj.openRocketVersion = proj.app.openrocket.versions.first()
			}
		}
	}
	
	override fun stop() {
		proj.app.openrocket.removeListener(openRocketListener)
		proj.removeListener(projectListener)
	}
	
	init {
		proj.app.openrocket.addListener(openRocketListener)
		proj.addListener(projectListener)
	}
}
