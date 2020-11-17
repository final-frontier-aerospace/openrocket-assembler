package com.ffaero.openrocketassembler.model

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ComponentOuterClass

class Component internal constructor(app: Application, private val proj: Project, internal val data: ComponentOuterClass.Component.Builder) {
	private val dispatcher: ApplicationListener = app.dispatcher
	
	public val id: Long
		get() = data.id
	
	public var name: String
		get() = data.name
		set(value) {
			val name = name
			if (value != name) {
				data.name = value
				dispatcher.onComponentRenamed(this, name, value)
			}
		}
	
	public var file: File
		get() = File(data.path)
		set(value) {
			val file = file
			if (value != file) {
				data.path = value.relativeTo(proj.file).getPath()
				dispatcher.onComponentRelocated(this, file, value)
			}
		}
	
	public fun reportUpdate() = dispatcher.onComponentUpdated(this)
	
	override fun equals(other: Any?): Boolean {
		if (!(other is Component)) {
			return false
		}
		return proj == other.proj && data == other.data
	}

	override fun hashCode(): Int = 31 * proj.hashCode() + data.hashCode()
	
	internal constructor(app: Application, proj: Project, file: File) : this(app, proj, ComponentOuterClass.Component.newBuilder().apply {
		id = proj.uniqueID
		name = "Untitled Component"
		path = file.relativeTo(proj.file).getPath()
	}) {
	}
}
