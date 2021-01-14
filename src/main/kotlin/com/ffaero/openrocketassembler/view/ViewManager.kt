package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.*
import java.io.Closeable
import javax.swing.UIManager

class ViewManager(private val app: ApplicationController) {
	private val views = HashMap<ProjectController, ApplicationView>()
	private val dialogs = HashSet<Closeable>()

	private val appListener = object : ApplicationAdapter() {
		override fun onProjectAdded(sender: ApplicationController, project: ProjectController) {
			views[project] = ApplicationView(this@ViewManager, project)
		}

		override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) {
			views.remove(project)
			possiblyClose()
		}
	}.apply {
		app.addListener(this)
	}

	private val settingsListener = object : SettingAdapter() {
		override fun onSettingsUpdated(sender: SettingController) {
			UIManager.setLookAndFeel(sender.lookAndFeel)
		}
	}.apply {
		onSettingsUpdated(app.settings)
		app.settings.addListener(this)
	}
	
	fun exit() {
		views.values.toTypedArray().forEach { it.close() }
		dialogs.toTypedArray().forEach { it.close() }
	}

	fun addDialog(frame: Closeable) = dialogs.add(frame)

	fun removeDialog(frame: Closeable) {
		dialogs.remove(frame)
		possiblyClose()
	}

	private fun possiblyClose() {
		if (views.isEmpty() && dialogs.isEmpty()) {
			app.stop()
			app.removeListener(appListener)
			app.settings.removeListener(settingsListener)
		}
	}
}
