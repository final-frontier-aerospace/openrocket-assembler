package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.HistoryTransaction
import com.ffaero.openrocketassembler.model.proto.ComponentOuterClass.Component
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

class ComponentController(val proj: ProjectController) : DispatcherBase<ComponentListener, ComponentListenerList>(ComponentListenerList()) {
	private val data = LinkedList<ComponentFile>()
	private val lookup = HashMap<Int, ComponentFile>()
	
	val components: List<File>
			get() = Collections.unmodifiableList(data)
	
	internal fun findComponent(id: Int): ComponentFile? = lookup[id]

	private fun add(index: Int, file: File, transact: HistoryTransaction) {
		val idx = if (index < 0) {
			data.size
		} else {
			index
		}
		val comp = ComponentFile(proj.makeID(), file)
		transact.add {
			data.add(idx, comp)
			lookup[comp.id] = comp
			listener.onComponentAdded(this, idx, comp)
		}.toUndo {
			data.removeAt(idx)
			lookup.remove(comp.id)
			listener.onComponentRemoved(this, idx)
		}
	}
	
	fun add(index: Int, file: File) {
		val transact = HistoryTransaction("Adding Component")
		add(index, file, transact)
		proj.history.perform(transact)
	}
	
	fun create(index: Int, file: File) {
		val transact = HistoryTransaction("Creating Component")
		transact.add {
			FileOutputStream(file).use {
				proj.componentTemplate.writeTo(it)
			}
		}.toUndo {
			file.delete()
		}
		add(index, file, transact)
		proj.history.perform(transact)
	}
	
	fun remove(index: Int) {
		val comp = data[index]
		val transact = HistoryTransaction("Removing Component")
		transact.add {
			data.removeAt(index)
			lookup.remove(comp.id)
			listener.onComponentRemoved(this, index)
		}.toUndo {
			data.add(index, comp)
			lookup[comp.id] = comp
			listener.onComponentAdded(this, index, comp)
		}
		proj.configurations.componentFileDeleted(comp, transact)
		proj.history.perform(transact)
	}
	
	fun move(fromIndex: Int, toIndex: Int) {
		val comp = data[fromIndex]
		proj.history.perform(HistoryTransaction("Moving Component").add {
			data.removeAt(fromIndex)
			data.add(toIndex, comp)
			listener.onComponentMoved(this, fromIndex, toIndex)
		}.toUndo {
			data.removeAt(toIndex)
			data.add(fromIndex, comp)
			listener.onComponentMoved(this, toIndex, fromIndex)
		})
	}
	
	fun change(index: Int, file: File) {
		val old = data[index]
		val repl = ComponentFile(old.id, file)
		val transact = HistoryTransaction("Relocating Component")
		transact.add {
			data[index] = repl
			listener.onComponentChanged(this, index, repl)
		}.toUndo {
			data[index] = old
			listener.onComponentChanged(this, index, old)
		}
		proj.configurations.componentFileUpdate(repl, old, transact)
		proj.history.perform(transact)
	}
	
	internal fun afterLoad(file: File?) {
		data.clear()
		lookup.clear()
		if (file != null) {
			val base = file.parentFile
			proj.model.componentsList.map {
				val comp = ComponentFile(it.id, File(base, it.filename))
				data.add(comp)
				lookup.put(comp.id, comp)
			}
		}
		listener.onComponentsReset(this, components)
	}
	
	internal fun beforeSave(file: File, model: Project.Builder) {
		val base = Paths.get(file.parentFile.absolutePath)
		model.clearComponents()
		data.forEach {
			model.addComponents(Component.newBuilder().setId(it.id).setFilename(base.relativize(Paths.get(it.path)).toString()).build())
		}
	}
}
