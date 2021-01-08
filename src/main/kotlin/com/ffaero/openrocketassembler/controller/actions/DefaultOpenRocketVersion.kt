package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.OpenRocketAdapter
import com.ffaero.openrocketassembler.controller.OpenRocketController
import com.ffaero.openrocketassembler.controller.ProjectAdapter
import com.ffaero.openrocketassembler.controller.ProjectController

class DefaultOpenRocketVersion : ActionBase<ProjectController>() {
	private val openRocketListener = object : OpenRocketAdapter() {
		override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) = enqueueActionAll(0)
	}
	
	private val projectListener = object : ProjectAdapter() {
		override fun onOpenRocketVersionChange(sender: ProjectController, version: String) = enqueueAction(sender, 0)
	}
	
	override fun runAction(controller: ProjectController) {
		val version = controller.openRocketVersion
		val versions = controller.app.openrocket.versions
		if ((version.isEmpty() || !versions.contains(version)) && !versions.isEmpty()) {
			controller.openRocketVersion = versions.first()
		}
	}

	override fun addListeners(controller: ProjectController) {
		controller.app.openrocket.addListener(openRocketListener)
		controller.addListener(projectListener)
	}

	override fun removeListeners(controller: ProjectController) {
		controller.app.openrocket.removeListener(openRocketListener)
		controller.removeListener(projectListener)
	}
}
