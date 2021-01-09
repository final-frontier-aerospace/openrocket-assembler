package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.proto.ComponentOuterClass.Component
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.forEach
import kotlin.collections.map

class ComponentController(val proj: ProjectController) : DispatcherBase<ComponentListener, ComponentListenerList>(ComponentListenerList()) {
	private val data = LinkedList<ComponentFile>()
	private val lookup = HashMap<Int, ComponentFile>()
	
	val components: List<File>
			get() = Collections.unmodifiableList(data)
	
	internal fun findComponent(id: Int): ComponentFile? = lookup[id]
	
	fun add(index: Int, file: File) {
		val idx = if (index < 0) {
			data.size
		} else {
			index
		}
		val comp = ComponentFile(proj.makeID(), file)
		data.add(idx, comp)
		lookup[comp.id] = comp
		proj.modified = true
		listener.onComponentAdded(this, idx, comp)
	}
	
	fun create(index: Int, file: File) {
		FileOutputStream(file).use {
			proj.componentTemplate.writeTo(it)
		}
		add(index, file)
	}
	
	fun remove(index: Int) {
		val comp = data.removeAt(index)
		lookup.remove(comp.id)
		proj.modified = true
		proj.configurations.componentFileDeleted(comp)
		listener.onComponentRemoved(this, index)
	}
	
	fun move(fromIndex: Int, toIndex: Int) {
		val tmp = data[fromIndex]
		data.removeAt(fromIndex)
		data.add(toIndex, tmp)
		proj.modified = true
		listener.onComponentMoved(this, fromIndex, toIndex)
	}
	
	fun change(index: Int, file: File) {
		val comp = ComponentFile(data[index].id, file)
		data[index] = comp
		proj.modified = true
		proj.configurations.componentFileUpdate(comp)
		listener.onComponentChanged(this, index, comp)
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
