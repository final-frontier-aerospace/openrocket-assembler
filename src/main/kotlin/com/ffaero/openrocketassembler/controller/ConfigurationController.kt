package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.Configuration
import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.ConfigurationOrBuilder
import java.io.File
import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.proto.Bug8188OuterClass.Bug8188

class ConfigurationController(public val proj: ProjectController) : DispatcherBase<ConfigurationListener, ConfigurationListenerList>(ConfigurationListenerList()) {
	public val names: List<String>
			get() = proj.model.getConfigurationsList().map { it.getName() }
	
	private fun componentsIn(model: ConfigurationOrBuilder): List<ComponentFile> = model.getComponentsList().map { proj.components.findComponent(it.getValue()) }.filterNotNull()
	public fun componentsAt(index: Int): List<File> = componentsIn(proj.model.getConfigurations(index))
	
	public fun add(name: String) {
		proj.model.addConfigurations(Configuration.newBuilder().setName(name).build())
		proj.modified = true
		listener.onConfigurationAdded(this, proj.model.getConfigurationsCount() - 1, name, listOf())
	}
	
	public fun duplicate(dupIndex: Int, newName: String) {
		val model = proj.model.getConfigurations(dupIndex).toBuilder().setName(newName).build()
		proj.model.addConfigurations(dupIndex + 1, model)
		proj.modified = true
		listener.onConfigurationAdded(this, dupIndex + 1, newName, componentsIn(model))
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
	
	public fun addComponent(configIndex: Int, index: Int, component: File) {
		if (component is ComponentFile) {
			proj.model.getConfigurationsBuilder(configIndex).addComponents(index, Bug8188.newBuilder().setValue(component.id).build())
			proj.modified = true
			listener.onComponentAdded(this, configIndex, index, component)
		}
	}
	
	public fun removeComponent(configIndex: Int, index: Int) {
		proj.model.getConfigurationsBuilder(configIndex).removeComponents(index)
		proj.modified = true
		listener.onComponentRemoved(this, configIndex, index)
	}
	
	public fun moveComponent(configIndex: Int, fromIndex: Int, toIndex: Int) {
		val cfg = proj.model.getConfigurationsBuilder(configIndex)
		val tmp = cfg.getComponents(fromIndex)
		cfg.removeComponents(fromIndex)
		cfg.addComponents(toIndex, tmp)
		proj.modified = true
		listener.onComponentMoved(this, configIndex, fromIndex, toIndex)
	}
	
	internal fun afterLoad() {
		listener.onConfigurationsReset(this, names)
	}
	
	internal fun componentFileUpdate(file: ComponentFile) {
		proj.model.getConfigurationsList().forEachIndexed { idx, it ->
			componentsIn(it).forEachIndexed { idx2, it2 ->
				if (it2.id == file.id) {
					listener.onComponentChanged(this, idx, idx2, file)
				}
			}
		}
	}
}
