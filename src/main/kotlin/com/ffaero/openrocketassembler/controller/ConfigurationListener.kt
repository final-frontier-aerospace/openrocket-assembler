package com.ffaero.openrocketassembler.controller

interface ConfigurationListener {
	fun onConfigurationsReset(sender: ConfigurationController, names: List<String>)
	fun onConfigurationAdded(sender: ConfigurationController, index: Int, name: String)
	fun onConfigurationRemoved(sender: ConfigurationController, index: Int)
	fun onConfigurationMoved(sender: ConfigurationController, fromIndex: Int, toIndex: Int)
	fun onConfigurationRenamed(sender: ConfigurationController, index: Int, name: String)
}
