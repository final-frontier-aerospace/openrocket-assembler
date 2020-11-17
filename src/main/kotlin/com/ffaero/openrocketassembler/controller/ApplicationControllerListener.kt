package com.ffaero.openrocketassembler.controller

interface ApplicationControllerListener {
	// ApplicationController
	public fun onStart(sender: ApplicationController)
	public fun onStop(sender: ApplicationController)
	
	// ProjectController
	public fun onProjectChange(sender: ProjectController, name: String?, modified: Boolean)
	
	// HistoryController
	public fun onHistoryChange(sender: HistoryController, undoAction: String?, redoAction: String?)
	
	// OpenRocketController
	public fun onOpenRocketVersionChange(sender: OpenRocketController, versionIndex: Int)
}
