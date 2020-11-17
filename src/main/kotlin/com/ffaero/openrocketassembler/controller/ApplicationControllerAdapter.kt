package com.ffaero.openrocketassembler.controller

open class ApplicationControllerAdapter : ApplicationControllerListener {
	override fun onStart(sender: ApplicationController) = Unit
	override fun onStop(sender: ApplicationController) = Unit
	override fun onProjectChange(sender: ProjectController, name: String?, modified: Boolean) = Unit
	override fun onHistoryChange(sender: HistoryController, undoAction: String?, redoAction: String?) = Unit
	override fun onOpenRocketVersionChange(sender: OpenRocketController, versionIndex: Int) = Unit
}
