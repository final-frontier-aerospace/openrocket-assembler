package com.ffaero.openrocketassembler.controller

open class ConfigurationAdapter : ConfigurationListener {
	override fun onConfigurationsReset(sender: ConfigurationController, names: List<String>) = Unit
	override fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String) = Unit
	override fun onConfigurationRemoved(sender: ConfigurationController, index: Int) = Unit
	override fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int) = Unit
	override fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String) = Unit
}
