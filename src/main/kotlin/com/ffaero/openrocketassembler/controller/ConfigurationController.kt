package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.Configuration

class ConfigurationController(public val proj: ProjectController) : DispatcherBase<ConfigurationListener, ConfigurationListenerList>(ConfigurationListenerList()) {
	public val names: List<String>
			get() = proj.model.getConfigurationsList().map { it.getName() }
	
	public fun add(name: String) {
		proj.model.addConfigurations(Configuration.newBuilder().setName(name).build())
		proj.modified = true
		listener.onConfigurationAdded(this, proj.model.getConfigurationsCount() - 1, name)
	}
	
	public fun remove(index: Int) {
		proj.model.removeConfigurations(index)
		proj.modified = true
		listener.onConfigurationRemoved(this, index)
	}
	
	public fun move(fromIndex: Int, toIndex: Int) {
		val tmp = proj.model.getConfigurations(fromIndex)
		proj.model.removeConfigurations(fromIndex)
		proj.model.addConfigurations(toIndex, tmp)
		proj.modified = true
		listener.onConfigurationMoved(this, fromIndex, toIndex)
	}
	
	public fun rename(index: Int, name: String) {
		proj.model.getConfigurationsBuilder(index).setName(name)
		proj.modified = true
		listener.onConfigurationRenamed(this, index, name)
	}
	
	internal fun afterLoad() {
		listener.onConfigurationsReset(this, names)
	}
}
