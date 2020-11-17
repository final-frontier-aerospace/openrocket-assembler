package com.ffaero.openrocketassembler.model

import java.io.File

interface ApplicationListener {
	// Application
	fun onProjectOpened(sender: Application, project: Project)
	fun onProjectClosed(sender: Application, project: Project)
	
	// Project
	fun onProjectRelocated(sender: Project, oldPath: File, newPath: File)
	fun onProjectModified(sender: Project, modified: Boolean)
	fun onProjectLoaded(sender: Project)
	fun onProjectSaved(sender: Project)
	fun onProjectOpenRocketVersionChanged(sender: Project, oldVersion: String, newVersion: String)
	fun onProjectComponentAdded(sender: Project, component: Component, index: Int)
	fun onProjectComponentRemoved(sender: Project, component: Component, index: Int)
	fun onProjectConfigurationAdded(sender: Project, configuration: Configuration, index: Int)
	fun onProjectConfigurationRemoved(sender: Project, configuration: Configuration, index: Int)
	
	// Component
	fun onComponentRenamed(sender: Component, oldName: String, newName: String)
	fun onComponentRelocated(sender: Component, oldPath: File, newPath: File)
	fun onComponentUpdated(sender: Component)
	
	// Configuration
	fun onConfigurationRenamed(sender: Configuration, oldName: String, newName: String)
	fun onConfigurationComponentAdded(sender: Configuration, component: Component, index: Int)
	fun onConfigurationComponentRemoved(sender: Configuration, component: Component, index: Int)
	fun onConfigurationTemplateRocketUpdated(sender: Configuration)
	fun onConfigurationTemplateComponentUpdated(sender: Configuration)
	fun onConfigurationSupportingFileAdded(sender: Configuration, file: SupportingFile, index: Int)
	fun onConfigurationSupportingFileRemoved(sender: Configuration, file: SupportingFile, index: Int)
}
