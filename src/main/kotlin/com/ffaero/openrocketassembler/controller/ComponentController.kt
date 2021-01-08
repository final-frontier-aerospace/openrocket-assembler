package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.ComponentFile
import com.ffaero.openrocketassembler.model.proto.ComponentOuterClass.Component
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.Collections
import java.util.LinkedList

class ComponentController(public val proj: ProjectController) : DispatcherBase<ComponentListener, ComponentListenerList>(ComponentListenerList()) {
	private val data = LinkedList<ComponentFile>()
	private val lookup = HashMap<Int, ComponentFile>()
	
	public val components: List<File>
			get() = Collections.unmodifiableList(data)
	
	internal fun findComponent(id: Int): ComponentFile? = lookup.get(id)
	
	public fun add(index: Int, file: File) {
		val idx: Int
		if (index < 0) {
			idx = data.size
		} else {
			idx = index
		}
		val comp = ComponentFile(proj.makeID(), file)
		data.add(idx, comp)
		lookup.put(comp.id, comp)
		proj.modified = true
		listener.onComponentAdded(this, idx, comp)
	}
	
	public fun create(index: Int, file: File) {
		FileOutputStream(file).use {
			proj.componentTemplate.writeTo(it)
		}
		add(index, file)
	}
	
	public fun remove(index: Int) {
		val comp = data.removeAt(index)
		lookup.remove(comp.id)
		proj.modified = true
		proj.configurations.componentFileDeleted(comp)
		listener.onComponentRemoved(this, index)
	}
	
	public fun move(fromIndex: Int, toIndex: Int) {
		val tmp = data.get(fromIndex)
		data.removeAt(fromIndex)
		data.add(toIndex, tmp)
		proj.modified = true
		listener.onComponentMoved(this, fromIndex, toIndex)
	}
	
	public fun change(index: Int, file: File) {
		val comp = ComponentFile(data.get(index).id, file)
		data.set(index, comp)
		proj.modified = true
		proj.configurations.componentFileUpdate(comp)
		listener.onComponentChanged(this, index, comp)
	}
	
	internal fun afterLoad(file: File?) {
		data.clear()
		lookup.clear()
		if (file != null) {
			val base = file.getParentFile()
			proj.model.getComponentsList().map {
				val comp = ComponentFile(it.getId(), File(base, it.getFilename()))
				data.add(comp)
				lookup.put(comp.id, comp)
			}
		}
		listener.onComponentsReset(this, components)
	}
	
	internal fun beforeSave(file: File, model: Project.Builder) {
		val base = Paths.get(file.getParentFile().getAbsolutePath())
		model.clearComponents()
		data.forEach {
			model.addComponents(Component.newBuilder().setId(it.id).setFilename(base.relativize(Paths.get(it.getPath())).toString()).build())
		}
	}
}
