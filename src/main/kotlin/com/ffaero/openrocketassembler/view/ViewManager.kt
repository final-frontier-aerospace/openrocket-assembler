package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ProjectController
import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController

class ViewManager(app: ApplicationController) {
	private val views = HashMap<ProjectController, ApplicationView>()
	
	public fun exit() = views.values.toTypedArray().forEach { it.close() }
	
	init {
		app.addListener(object : ApplicationAdapter() {
			override fun onProjectAdded(sender: ApplicationController, project: ProjectController) {
				views.put(project, ApplicationView(this@ViewManager, project))
			}
			
			override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) {
				views.remove(project)
			}
		})
	}
}
