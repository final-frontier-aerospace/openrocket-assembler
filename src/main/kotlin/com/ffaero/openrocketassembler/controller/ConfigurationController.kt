package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.proto.Bug8188OuterClass.Bug8188
import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.Configuration
import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass.ConfigurationOrBuilder
import com.google.protobuf.ByteString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils

class ConfigurationController(public val proj: ProjectController) : DispatcherBase<ConfigurationListener, ConfigurationListenerList>(ConfigurationListenerList()) {
	public val names: List<String>
			get() = proj.model.getConfigurationsList().map { it.getName() }
	
	private fun componentsIn(model: ConfigurationOrBuilder): List<ComponentFile> = model.getComponentsList().map { proj.components.findComponent(it.getValue()) }.filterNotNull()
	public fun componentsAt(index: Int): List<File> = componentsIn(proj.model.getConfigurations(index))
	
	public fun getFileOutlineAt(index: Int): ByteString {
		val file = proj.model.getConfigurations(index).getFileOutline()
		if (file.isEmpty()) {
			return FileFormat.emptyORK
		} else {
			return file
		}
	}
	
	public fun setFileOutlineAt(index: Int, file: ByteString) {
		if (!getFileOutlineAt(index).equals(file)) {
			proj.model.getConfigurationsBuilder(index).setFileOutline(file)
			proj.modified = true
		}
	}
	
	private val editing = ArrayList<Boolean>()
	
	public fun add(name: String) {
		proj.model.addConfigurations(Configuration.newBuilder().setName(name).build())
		proj.modified = true
		editing.add(false)
		listener.onConfigurationAdded(this, proj.model.getConfigurationsCount() - 1, name, listOf())
	}
	
	public fun duplicate(dupIndex: Int, newName: String) {
		val model = proj.model.getConfigurations(dupIndex).toBuilder().setName(newName).build()
		proj.model.addConfigurations(dupIndex + 1, model)
		proj.modified = true
		editing.add(dupIndex + 1, false)
		listener.onConfigurationAdded(this, dupIndex + 1, newName, componentsIn(model))
	}
	
	public fun remove(index: Int) {
		proj.model.removeConfigurations(index)
		proj.modified = true
		editing.removeAt(index)
		listener.onConfigurationRemoved(this, index)
	}
	
	public fun move(fromIndex: Int, toIndex: Int) {
		val tmp = proj.model.getConfigurations(fromIndex)
		proj.model.removeConfigurations(fromIndex)
		proj.model.addConfigurations(toIndex, tmp)
		proj.modified = true
		val tmpB = editing.get(fromIndex)
		editing.removeAt(fromIndex)
		editing.add(toIndex, tmpB)
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
		editing.clear()
		names.forEach {
			editing.add(false)
		}
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
	
	internal fun componentFileDeleted(file: ComponentFile) {
		val list = ArrayList<Pair<Int, Int>>()
		proj.model.getConfigurationsList().forEachIndexed { idx, it ->
			it.getComponentsList().forEachIndexed { idx2, it2 ->
				if (it2.getValue() == file.id) {
					list.add(Pair(idx, idx2))
				}
			}
		}
		list.reversed().forEach {
			removeComponent(it.first, it.second)
		}
	}
	
	public fun isEditingAny() = editing.any { it }
	public fun isEditing(configIndex: Int) = editing.get(configIndex)
	
	public fun edit(configIndex: Int) {
		if (isEditing(configIndex)) {
			return
		}
		editing.set(configIndex, true)
		val model = proj.model.getConfigurationsBuilder(configIndex)
		val file = FileSystem.getTempFile(model, model.getName() + ".ork")
		OpenRocketOutputStream(FileOutputStream(file)).use { out ->
			OpenRocketInputStream(model.getFileOutline().newInput()).use { outlineIn ->
				while (true) {
					val outlineEnt = outlineIn.getNextEntry()
					if (outlineEnt == null) {
						break
					}
					if (outlineEnt.isRocketXML) {
						val doc = OpenRocketXML(UncloseableInputStream(outlineIn))
						componentsIn(model).forEach {
							OpenRocketInputStream(FileInputStream(it)).use { compIn ->
								while (true) {
									val compEnt = compIn.getNextEntry()
									if (compEnt == null) {
										break
									}
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
		proj.app.openrocket.launch(proj.openRocketVersion, file.getAbsolutePath()) {
			val bs = ByteString.newOutput()
			OpenRocketOutputStream(bs).use { out ->
				OpenRocketInputStream(FileInputStream(file)).use { input ->
					while (true) {
						val ent = input.getNextEntry()
						if (ent == null) {
							break
						}
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
			editing.set(configIndex, false)
		}
	}
}
