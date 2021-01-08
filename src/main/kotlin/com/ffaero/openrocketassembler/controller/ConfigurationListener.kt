package com.ffaero.openrocketassembler.controller

import java.io.File

interface ConfigurationListener {
	fun onConfigurationsReset(sender: ConfigurationController, names: List<String>)
	fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String, components: List<File>)
	fun onConfigurationRemoved(sender: ConfigurationController, index: Int)
	fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int)
	fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String)
	fun onComponentAdded(sender: ConfigurationController, configIndex: Int, index: Int, component: File)
	fun onComponentRemoved(sender: ConfigurationController, configIndex: Int, index: Int)
	fun onComponentMoved(sender: ConfigurationController, configIndex: Int, fromIndex: Int, toIndex: Int)
	fun onComponentChanged(sender: ConfigurationController, configIndex: Int, index: Int, component: File)
}
