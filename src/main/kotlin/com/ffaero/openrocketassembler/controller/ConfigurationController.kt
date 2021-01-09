package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.FileFormat
import com.ffaero.openrocketassembler.FileSystem
import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.HistoryTransaction
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
		val old = getFileOutlineAt(index)
		if (old != file) {
			proj.history.perform(HistoryTransaction("Editing Assembly File").add {
				proj.model.getConfigurationsBuilder(index).fileOutline = file
			}.toUndo {
				proj.model.getConfigurationsBuilder(index).fileOutline = old
			})
		}
	}
	
	private val editing = ArrayList<Boolean>()

	private fun add(desc: String, index: Int, model: Configuration) = proj.history.perform(HistoryTransaction(desc).add {
		proj.model.addConfigurations(index, model)
		editing.add(index, false)
		listener.onConfigurationAdded(this, index, model.name, listOf())
	}.toUndo {
		proj.model.removeConfigurations(index)
		editing.removeAt(index)
		listener.onConfigurationRemoved(this, index)
	})
	
	fun add(name: String) = add("Adding Configuration", proj.model.configurationsCount, Configuration.newBuilder().setName(name).build())
	fun duplicate(dupIndex: Int, newName: String) = add("Duplicating Configuration", dupIndex + 1, proj.model.getConfigurations(dupIndex).toBuilder().setName(newName).build())
	
	fun remove(index: Int) {
		val config = proj.model.getConfigurations(index)
		val isEditing = editing[index]
		proj.history.perform(HistoryTransaction("Removing Configuration").add {
			proj.model.removeConfigurations(index)
			editing.removeAt(index)
			listener.onConfigurationRemoved(this, index)
		}.toUndo {
			proj.model.addConfigurations(index, config)
			editing.add(index, isEditing)
			listener.onConfigurationAdded(this, index, config.name, componentsIn(config))
		})
	}
	
	fun move(fromIndex: Int, toIndex: Int) {
		val config = proj.model.getConfigurations(fromIndex)
		val isEditing = editing[fromIndex]
		proj.history.perform(HistoryTransaction("Moving Configuration").add {
			proj.model.removeConfigurations(fromIndex)
			proj.model.addConfigurations(toIndex, config)
			editing.removeAt(fromIndex)
			editing.add(toIndex, isEditing)
			listener.onConfigurationMoved(this, fromIndex, toIndex)
		}.toUndo {
			proj.model.removeConfigurations(toIndex)
			proj.model.addConfigurations(fromIndex, config)
			editing.removeAt(toIndex)
			editing.add(fromIndex, isEditing)
			listener.onConfigurationMoved(this, toIndex, fromIndex)
		})
	}
	
	fun rename(index: Int, name: String) {
		val model = proj.model.getConfigurationsBuilder(index)
		val oldName = model.name
		proj.history.perform(HistoryTransaction("Renaming Configuration").add {
			model.name = name
			listener.onConfigurationRenamed(this, index, name)
		}.toUndo {
			model.name = oldName
			listener.onConfigurationRenamed(this, index, oldName)
		})
	}
	
	fun addComponent(configIndex: Int, index: Int, component: File) {
		if (component is ComponentFile) {
			val model = proj.model.getConfigurationsBuilder(configIndex)
			val comp = Bug8188.newBuilder().setValue(component.id).build()
			proj.history.perform(HistoryTransaction("Adding Component to Configuration").add {
				model.addComponents(index, comp)
				listener.onComponentAdded(this, configIndex, index, component)
			}.toUndo {
				model.removeComponents(index)
				listener.onComponentRemoved(this, configIndex, index)
			})
		}
	}

	private fun removeComponent(configIndex: Int, index: Int, transact: HistoryTransaction) {
		val model = proj.model.getConfigurationsBuilder(configIndex)
		val comp = model.getComponents(index)
		val file = proj.components.findComponent(comp.value)
		if (file != null) {
			transact.add {
				model.removeComponents(index)
				listener.onComponentRemoved(this, configIndex, index)
			}.toUndo {
				model.addComponents(index, comp)
				listener.onComponentAdded(this, configIndex, index, file)
			}
		}
	}
	
	fun removeComponent(configIndex: Int, index: Int) {
		val transact = HistoryTransaction("Removing Component from Configuration")
		removeComponent(configIndex, index, transact)
		proj.history.perform(transact)
	}
	
	fun moveComponent(configIndex: Int, fromIndex: Int, toIndex: Int) {
		val model = proj.model.getConfigurationsBuilder(configIndex)
		val comp = model.getComponents(fromIndex)
		proj.history.perform(HistoryTransaction("Moving Component within Configuration").add {
			model.removeComponents(fromIndex)
			model.addComponents(toIndex, comp)
			listener.onComponentMoved(this, configIndex, fromIndex, toIndex)
		}.toUndo {
			model.removeComponents(toIndex)
			model.addComponents(fromIndex, comp)
			listener.onComponentMoved(this, configIndex, toIndex, fromIndex)
		})
	}
	
	internal fun afterLoad() {
		editing.clear()
		names.forEach { _ ->
			editing.add(false)
		}
		listener.onConfigurationsReset(this, names)
	}
	
	internal fun componentFileUpdate(file: ComponentFile, oldFile: ComponentFile, transact: HistoryTransaction) {
		val list = ArrayList<Pair<Int, Int>>()
		proj.model.configurationsList.forEachIndexed { idx, it ->
			componentsIn(it).forEachIndexed { idx2, it2 ->
				if (it2.id == file.id) {
					list.add(Pair(idx, idx2))
					listener.onComponentChanged(this, idx, idx2, file)
				}
			}
		}
		transact.add {
			list.forEach {
				listener.onComponentChanged(this, it.first, it.second, file)
			}
		}.toUndo {
			list.forEach {
				listener.onComponentChanged(this, it.first, it.second, oldFile)
			}
		}
	}
	
	internal fun componentFileDeleted(file: ComponentFile, transact: HistoryTransaction) {
		val list = ArrayList<Pair<Int, Int>>()
		proj.model.configurationsList.forEachIndexed { idx, it ->
			it.componentsList.forEachIndexed { idx2, it2 ->
				if (it2.value == file.id) {
					list.add(Pair(idx, idx2))
				}
			}
		}
		list.reversed().forEach {
			removeComponent(it.first, it.second, transact)
		}
	}
	
	fun isEditingAny() = editing.any { it }
	
	fun edit(configIndex: Int) {
		if (editing[configIndex]) {
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
