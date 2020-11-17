package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.Application

class ApplicationController(app: Application) {
	public val project: ProjectController = ProjectController(app)
	public val component: ComponentController = ComponentController(app)
	public val configuration: ConfigurationController = ConfigurationController(app)
	public val history: HistoryController = HistoryController(app)
	public val openRocket: OpenRocketController = OpenRocketController(app)
	public val recent: RecentController = RecentController(app)
	
	private val listeners: MutableSet<ApplicationControllerListener> = HashSet<ApplicationControllerListener>()
	internal val dispatcher: ApplicationControllerListener = object : ApplicationControllerListener {
		override fun onStart(sender: ApplicationController) = listeners.forEach { it.onStart(sender) }
		override fun onStop(sender: ApplicationController) = listeners.forEach { it.onStop(sender) }
		override fun onProjectChange(sender: ProjectController, name: String?, modified: Boolean) = listeners.forEach { it.onProjectChange(sender, name, modified) }
		override fun onHistoryChange(sender: HistoryController, undoAction: String?, redoAction: String?) = listeners.forEach { it.onHistoryChange(sender, undoAction, redoAction) }
		override fun onOpenRocketVersionChange(sender: OpenRocketController, versionIndex: Int) = listeners.forEach { it.onOpenRocketVersionChange(sender, versionIndex) }
	}
	
	public fun addListener(listener: ApplicationControllerListener) = listeners.add(listener)
	
	public fun removeListener(listener: ApplicationControllerListener) = listeners.remove(listener)
	
	public fun start() {
		dispatcher.onStart(this)
	}
	
	public fun exit() {
		dispatcher.onStop(this)
	}
}
