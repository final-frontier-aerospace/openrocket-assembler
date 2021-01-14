package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.*
import org.slf4j.LoggerFactory
import java.awt.EventQueue
import java.io.Closeable
import javax.swing.UIManager

class ViewManager(private val app: ApplicationController) {
	companion object {
		private val log = LoggerFactory.getLogger(ViewManager::class.java)
	}

	private val views = HashMap<ProjectController, ApplicationView>()
	private val dialogs = HashSet<Closeable>()

	private val appListener = object : ApplicationAdapter() {
		override fun onProjectAdded(sender: ApplicationController, project: ProjectController) = EventQueue.invokeLater {
			log.info("Project added to view")
			views[project] = ApplicationView(this@ViewManager, project)
		}

		override fun onProjectRemoved(sender: ApplicationController, project: ProjectController) {
			log.info("Project removed from view")
			views.remove(project)
			possiblyClose()
		}
	}.apply {
		app.addListener(this)
	}

	private val settingsListener = object : SettingAdapter() {
		override fun onSettingsUpdated(sender: SettingController) = EventQueue.invokeLater {
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

	init {
		log.info("View initialized")
	}
}
