package com.ffaero.openrocketassembler.model

import java.io.File

class Application {
	private var _project: Project? = null
	private val listeners: MutableSet<ApplicationListener> = HashSet<ApplicationListener>()
	
	internal val dispatcher: ApplicationListener = object : ApplicationListener {
		override fun onProjectOpened(sender: Application, project: Project) = listeners.forEach { it.onProjectOpened(sender, project) }
		override fun onProjectClosed(sender: Application, project: Project) = listeners.forEach { it.onProjectClosed(sender, project) }
		override fun onProjectRelocated(sender: Project, oldPath: File, newPath: File) = listeners.forEach { it.onProjectRelocated(sender, oldPath, newPath) }
		override fun onProjectModified(sender: Project, modified: Boolean) = listeners.forEach { it.onProjectModified(sender, modified) }
		override fun onProjectLoaded(sender: Project) = listeners.forEach { it.onProjectLoaded(sender) }
		override fun onProjectSaved(sender: Project) = listeners.forEach { it.onProjectSaved(sender) }
		override fun onProjectOpenRocketVersionChanged(sender: Project, oldVersion: String, newVersion: String) = listeners.forEach { it.onProjectOpenRocketVersionChanged(sender, oldVersion, newVersion) }
		override fun onProjectComponentAdded(sender: Project, component: Component, index: Int) = listeners.forEach { it.onProjectComponentAdded(sender, component, index) }
		override fun onProjectComponentRemoved(sender: Project, component: Component, index: Int) = listeners.forEach { it.onProjectComponentRemoved(sender, component, index) }
		override fun onProjectConfigurationAdded(sender: Project, configuration: Configuration, index: Int) = listeners.forEach { it.onProjectConfigurationAdded(sender, configuration, index) }
		override fun onProjectConfigurationRemoved(sender: Project, configuration: Configuration, index: Int) = listeners.forEach { it.onProjectConfigurationRemoved(sender, configuration, index) }
		override fun onComponentRenamed(sender: Component, oldName: String, newName: String) = listeners.forEach { it.onComponentRenamed(sender, oldName, newName) }
		override fun onComponentRelocated(sender: Component, oldPath: File, newPath: File) = listeners.forEach { it.onComponentRelocated(sender, oldPath, newPath) }
		override fun onComponentUpdated(sender: Component) = listeners.forEach { it.onComponentUpdated(sender) }
		override fun onConfigurationRenamed(sender: Configuration, oldName: String, newName: String) = listeners.forEach { it.onConfigurationRenamed(sender, oldName, newName) }
		override fun onConfigurationComponentAdded(sender: Configuration, component: Component, index: Int) = listeners.forEach { it.onConfigurationComponentAdded(sender, component, index) }
		override fun onConfigurationComponentRemoved(sender: Configuration, component: Component, index: Int) = listeners.forEach { it.onConfigurationComponentRemoved(sender, component, index) }
		override fun onConfigurationTemplateRocketUpdated(sender: Configuration) = listeners.forEach { it.onConfigurationTemplateRocketUpdated(sender) }
		override fun onConfigurationTemplateComponentUpdated(sender: Configuration) = listeners.forEach { it.onConfigurationTemplateComponentUpdated(sender) }
		override fun onConfigurationSupportingFileAdded(sender: Configuration, file: SupportingFile, index: Int) = listeners.forEach { it.onConfigurationSupportingFileAdded(sender, file, index) }
		override fun onConfigurationSupportingFileRemoved(sender: Configuration, file: SupportingFile, index: Int) = listeners.forEach { it.onConfigurationSupportingFileRemoved(sender, file, index) }
	}
	
	public var project: Project?
		get() = _project
		set(value) {
			val project = project
			if (value != project) {
				if (project !== null) {
					dispatcher.onProjectClosed(this, project)
				}
				_project = value
				if (value !== null) {
					dispatcher.onProjectOpened(this, value)
				}
			}
		}
	
	public fun addListener(listener: ApplicationListener) = listeners.add(listener)
	
	public fun removeListener(listener: ApplicationListener) = listeners.remove(listener)
}
