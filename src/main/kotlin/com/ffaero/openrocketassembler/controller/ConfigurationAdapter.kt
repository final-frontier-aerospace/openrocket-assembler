package com.ffaero.openrocketassembler.controller

import java.io.File

open class ConfigurationAdapter : ConfigurationListener {
	override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) = Unit
	override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String, components: List<File>) = Unit
	override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) = Unit
	override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) = Unit
	override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) = Unit
	override fun onComponentAdded(sender: ConfigurationController, configIndex: Int, index: Int, component: File) = Unit
	override fun onComponentRemoved(sender: ConfigurationController, configIndex: Int, index: Int) = Unit
	override fun onComponentMoved(sender: ConfigurationController, configIndex: Int, fromIndex: Int, toIndex: Int) = Unit
	override fun onComponentChanged(sender: ConfigurationController, configIndex: Int, index: Int, component: File) = Unit
}
