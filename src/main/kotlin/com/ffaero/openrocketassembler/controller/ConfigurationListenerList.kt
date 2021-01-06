package com.ffaero.openrocketassembler.controller

class ConfigurationListenerList : ListenerListBase<ConfigurationListener>(), ConfigurationListener {
	override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) = forEach { it.onConfigurationsReset(sender, names) }
	override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String) = forEach { it.onConfigurationAdded(sender, index, name) }
	override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) = forEach { it.onConfigurationRemoved(sender, index) }
	override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) = forEach { it.onConfigurationMoved(sender, fromIndex, toIndex) }
	override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) = forEach { it.onConfigurationRenamed(sender, index, name) }
}
