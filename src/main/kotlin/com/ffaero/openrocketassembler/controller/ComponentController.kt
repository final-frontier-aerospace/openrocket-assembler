package com.ffaero.openrocketassembler.controller

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ComponentOuterClass.Component
import java.nio.file.Paths
import java.nio.file.Path
import com.ffaero.openrocketassembler.model.AbsoluteComponent
import java.util.LinkedList
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass.Project
import java.io.FileOutputStream

class ComponentController(public val proj: ProjectController) : DispatcherBase<ComponentListener, ComponentListenerList>(ComponentListenerList()) {
	private val data = LinkedList<AbsoluteComponent>()
	
	public val components: List<File>
			get() = data.map { it.file }
	
	public fun add(file: File) {
		val abs = file.getAbsoluteFile()
		data.add(AbsoluteComponent(proj.makeID(), abs))
		proj.modified = true
		listener.onComponentAdded(this, data.size - 1, abs)
	}
	
	public fun create(file: File) {
		FileOutputStream(file).use {
			proj.componentTemplate.writeTo(it)
		}
		add(file)
	}
	
	public fun remove(index: Int) {
		data.removeAt(index)
		proj.modified = true
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
		val abs = file.getAbsoluteFile()
		data.get(index).file = abs
		proj.modified = true
		listener.onComponentChanged(this, index, abs)
	}
	
	internal fun afterLoad(file: File?) {
		data.clear()
		if (file != null) {
			val base = file.getParentFile()
			data.addAll(proj.model.getComponentsList().map { AbsoluteComponent(it.getId(), File(base, it.getFilename()).getAbsoluteFile()) })
		}
		listener.onComponentsReset(this, components)
	}
	
	internal fun beforeSave(file: File, model: Project.Builder) {
		val base = Paths.get(file.getParentFile().getAbsolutePath())
		model.clearComponents()
		data.forEach {
			model.addComponents(Component.newBuilder().setId(it.id).setFilename(base.relativize(Paths.get(it.file.getPath())).toString()).build())
		}
	}
}
