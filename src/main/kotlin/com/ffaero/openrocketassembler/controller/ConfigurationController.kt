package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.proto.Bug8188OuterClass.Bug8188
import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.Configuration
import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.ConfigurationOrBuilder
import com.google.protobuf.ByteString
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ConfigurationController(val proj: ProjectController) : DispatcherBase<ConfigurationListener, ConfigurationListenerList>(ConfigurationListenerList()) {
	val names: List<String>
			get() = proj.model.configurationsList.map { it.name }
	
	private fun componentsIn(model: ConfigurationOrBuilder): List<ComponentFile> = model.componentsList.mapNotNull { proj.components.findComponent(it.value) }
	fun componentsAt(index: Int): List<File> = componentsIn(proj.model.getConfigurations(index))
	
	private fun getFileOutlineAt(index: Int): ByteString {
		val file = proj.model.getConfigurations(index).fileOutline
		return if (file.isEmpty) {
			FileFormat.emptyORK
		} else {
			file
		}
	}
	
	private fun setFileOutlineAt(index: Int, file: ByteString) {
		if (getFileOutlineAt(index) != file) {
			proj.model.getConfigurationsBuilder(index).fileOutline = file
			proj.modified = true
		}
	}
	
	private val editing = ArrayList<Boolean>()
	
	fun add(name: String) {
		proj.model.addConfigurations(Configuration.newBuilder().setName(name).build())
		proj.modified = true
		editing.add(false)
		listener.onConfigurationAdded(this, proj.model.configurationsCount - 1, name, listOf())
	}
	
	fun duplicate(dupIndex: Int, newName: String) {
		val model = proj.model.getConfigurations(dupIndex).toBuilder().setName(newName).build()
		proj.model.addConfigurations(dupIndex + 1, model)
		proj.modified = true
		editing.add(dupIndex + 1, false)
		listener.onConfigurationAdded(this, dupIndex + 1, newName, componentsIn(model))
	}
	
	fun remove(index: Int) {
		proj.model.removeConfigurations(index)
		proj.modified = true
		editing.removeAt(index)
		listener.onConfigurationRemoved(this, index)
	}
	
	fun move(fromIndex: Int, toIndex: Int) {
		val tmp = proj.model.getConfigurations(fromIndex)
		proj.model.removeConfigurations(fromIndex)
		proj.model.addConfigurations(toIndex, tmp)
		proj.modified = true
		val tmpB = editing[fromIndex]
		editing.removeAt(fromIndex)
		editing.add(toIndex, tmpB)
		listener.onConfigurationMoved(this, fromIndex, toIndex)
	}
	
	fun rename(index: Int, name: String) {
		proj.model.getConfigurationsBuilder(index).name = name
		proj.modified = true
		listener.onConfigurationRenamed(this, index, name)
	}
	
	fun addComponent(configIndex: Int, index: Int, component: File) {
		if (component is ComponentFile) {
			proj.model.getConfigurationsBuilder(configIndex).addComponents(index, Bug8188.newBuilder().setValue(component.id).build())
			proj.modified = true
			listener.onComponentAdded(this, configIndex, index, component)
		}
	}
	
	fun removeComponent(configIndex: Int, index: Int) {
		proj.model.getConfigurationsBuilder(configIndex).removeComponents(index)
		proj.modified = true
		listener.onComponentRemoved(this, configIndex, index)
	}
	
	fun moveComponent(configIndex: Int, fromIndex: Int, toIndex: Int) {
		val cfg = proj.model.getConfigurationsBuilder(configIndex)
		val tmp = cfg.getComponents(fromIndex)
		cfg.removeComponents(fromIndex)
		cfg.addComponents(toIndex, tmp)
		proj.modified = true
		listener.onComponentMoved(this, configIndex, fromIndex, toIndex)
	}
	
	internal fun afterLoad() {
		editing.clear()
		names.forEach { _ ->
			editing.add(false)
		}
		listener.onConfigurationsReset(this, names)
	}
	
	internal fun componentFileUpdate(file: ComponentFile) {
		proj.model.configurationsList.forEachIndexed { idx, it ->
			componentsIn(it).forEachIndexed { idx2, it2 ->
				if (it2.id == file.id) {
					listener.onComponentChanged(this, idx, idx2, file)
				}
			}
		}
	}
	
	internal fun componentFileDeleted(file: ComponentFile) {
		val list = ArrayList<Pair<Int, Int>>()
		proj.model.configurationsList.forEachIndexed { idx, it ->
			it.componentsList.forEachIndexed { idx2, it2 ->
				if (it2.value == file.id) {
					list.add(Pair(idx, idx2))
				}
			}
		}
		list.reversed().forEach {
			removeComponent(it.first, it.second)
		}
	}
	
	fun isEditingAny() = editing.any { it }
	private fun isEditing(configIndex: Int) = editing[configIndex]
	
	fun edit(configIndex: Int) {
		if (isEditing(configIndex)) {
			return
		}
		editing[configIndex] = true
		val model = proj.model.getConfigurationsBuilder(configIndex)
		val file = FileSystem.getTempFile(model, model.name + ".ork")
		OpenRocketOutputStream(FileOutputStream(file)).use { out ->
			OpenRocketInputStream(getFileOutlineAt(configIndex).newInput()).use { outlineIn ->
				while (true) {
					val outlineEnt = outlineIn.getNextEntry() ?: break
					if (outlineEnt.isRocketXML) {
						val doc = OpenRocketXML(UncloseableInputStream(outlineIn))
						componentsIn(model).forEach {
							OpenRocketInputStream(FileInputStream(it)).use { compIn ->
								while (true) {
									val compEnt = compIn.getNextEntry() ?: break
									if (compEnt.isRocketXML) {
										doc.addSubcomponents(OpenRocketXML(UncloseableInputStream(compIn)).getSubcomponents())
									} else {
										out.putNextEntry(compEnt)
										IOUtils.copy(UncloseableInputStream(compIn), UncloseableOutputStream(out))
										out.closeEntry()
									}
									compIn.closeEntry()
								}
							}
						}
						out.putNextEntry(OpenRocketEntry.rocketXMLEntry)
						doc.write(UncloseableOutputStream(out))
						out.closeEntry()
					} else {
						out.putNextEntry(outlineEnt)
						IOUtils.copy(UncloseableInputStream(outlineIn), UncloseableOutputStream(out))
						out.closeEntry()
					}
					outlineIn.closeEntry()
				}
			}
		}
		proj.app.openrocket.launch(proj.openRocketVersion, file.absolutePath) {
			val bs = ByteString.newOutput()
			OpenRocketOutputStream(bs).use { out ->
				OpenRocketInputStream(FileInputStream(file)).use { input ->
					while (true) {
						val ent = input.getNextEntry() ?: break
						if (ent.isRocketXML) {
							val doc = OpenRocketXML(UncloseableInputStream(input))
							doc.clearSubcomponents()
							out.putNextEntry(OpenRocketEntry.rocketXMLEntry)
							doc.write(UncloseableOutputStream(out))
							out.closeEntry()
						} else {
							out.putNextEntry(ent)
							IOUtils.copy(UncloseableInputStream(input), UncloseableOutputStream(out))
							out.closeEntry()
						}
						input.closeEntry()
					}
				}
			}
			file.delete()
			setFileOutlineAt(configIndex, bs.toByteString())
			editing[configIndex] = false
		}
	}
}
