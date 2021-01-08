package com.ffaero.openrocketassembler.controller

import java.io.File

class ConfigurationListenerList : ListenerListBase<ConfigurationListener>(), ConfigurationListener {
	override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) = forEach { it.onConfigurationsReset(sender, names) }
	override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String, components: List<File>) = forEach { it.onConfigurationAdded(sender, index, name, components) }
	override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) = forEach { it.onConfigurationRemoved(sender, index) }
	override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) = forEach { it.onConfigurationMoved(sender, fromIndex, toIndex) }
	override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) = forEach { it.onConfigurationRenamed(sender, index, name) }
	override fun onComponentAdded(sender: ConfigurationController, configIndex: Int, index: Int, component: File) = forEach { it.onComponentAdded(sender, configIndex, index, component) }
	override fun onComponentRemoved(sender: ConfigurationController, configIndex: Int, index: Int) = forEach { it.onComponentRemoved(sender, configIndex, index) }
	override fun onComponentMoved(sender: ConfigurationController, configIndex: Int, fromIndex: Int, toIndex: Int) = forEach { it.onComponentMoved(sender, configIndex, fromIndex, toIndex) }
	override fun onComponentChanged(sender: ConfigurationController, configIndex: Int, index: Int, component: File) = forEach { it.onComponentChanged(sender, configIndex, index, component) }
}
