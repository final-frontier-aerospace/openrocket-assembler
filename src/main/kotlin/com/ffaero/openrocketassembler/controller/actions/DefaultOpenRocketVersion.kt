package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.OpenRocketController
import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.OpenRocketAdapter

class DefaultOpenRocketVersion(private val proj: ProjectController) : IAction {
	private fun fixVersion(version: String, versions: List<String>) {
		if ((version.isEmpty() || !versions.contains(version)) && !versions.isEmpty()) {
			proj.openRocketVersion = versions.first()
		}
	}
	
	private val openRocketListener = object : OpenRocketAdapter() {
		override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) = fixVersion(proj.openRocketVersion, versions)
	}
	
	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = fixVersion(version, proj.app.openrocket.versions)
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
