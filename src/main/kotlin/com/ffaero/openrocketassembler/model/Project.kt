package com.ffaero.openrocketassembler.model

import java.io.File
import com.ffaero.openrocketassembler.model.proto.ProjectOuterClass

class Project(app: Application, private var _file: File) {
	private class ComponentList(private val app: Application, private val proj: Project) : ModelList<Component>() {
		override val size: Int
			get() = proj.data.getComponentsCount()

		override fun add(index: Int, element: Component) {
			proj.data.getComponentsBuilderList().add(index, element.data)
			proj.componentMap.put(element.id, element)
			proj.dispatcher.onProjectComponentAdded(proj, element, index)
		}
	
		override fun get(index: Int): Component = Component(app, proj, proj.data.getComponentsBuilderList().get(index))
		
		override fun equals(other: Any?): Boolean {
			if (!(other is ComponentList)) {
				return false;
			}
			return proj == other.proj
		}
		
		override fun hashCode(): Int = proj.hashCode()
		
		override fun indexOf(element: Component): Int = proj.data.getComponentsBuilderList().indexOf(element.data)
		
		override fun lastIndexOf(element: Component): Int = proj.data.getComponentsBuilderList().lastIndexOf(element.data)
		
		override fun removeAt(index: Int): Component {
			val v = Component(app, proj, proj.data.getComponentsBuilderList().removeAt(index))
			proj.componentMap.remove(v.id)
			proj.dispatcher.onProjectComponentRemoved(proj, v, index)
			return v
		}
	}
	
	private class ConfigurationList(private val app: Application, private val proj: Project) : ModelList<Configuration>() {
		override val size: Int
			get() = proj.data.getConfigurationsCount()

		override fun add(index: Int, element: Configuration) {
			proj.data.getConfigurationsBuilderList().add(index, element.data)
			proj.dispatcher.onProjectConfigurationAdded(proj, element, index)
		}
	
		override fun get(index: Int): Configuration = Configuration(app, proj, proj.data.getConfigurationsBuilderList().get(index))
		
		override fun equals(other: Any?): Boolean {
			if (!(other is ConfigurationList)) {
				return false;
			}
			return proj == other.proj
		}
		
		override fun hashCode(): Int = proj.hashCode()
		
		override fun indexOf(element: Configuration): Int = proj.data.getConfigurationsBuilderList().indexOf(element.data)
		
		override fun lastIndexOf(element: Configuration): Int = proj.data.getConfigurationsBuilderList().lastIndexOf(element.data)
		
		override fun removeAt(index: Int): Configuration {
			val v = Configuration(app, proj, proj.data.getConfigurationsBuilderList().removeAt(index))
			proj.dispatcher.onProjectConfigurationRemoved(proj, v, index)
			return v
		}
	}
	
	private val dispatcher: ApplicationListener = app.dispatcher
	private var data: ProjectOuterClass.Project.Builder = ProjectOuterClass.Project.newBuilder()
	private var _modified: Boolean = false
	private val componentMap: MutableMap<Long, Component> = HashMap<Long, Component>()
	
	public var file: File
		get() = _file
		set(value) {
			val file = file
			if (value != file) {
				_file = value
				dispatcher.onProjectRelocated(this, file, value)
			}
		}
	
	public var modified: Boolean
		get() = _modified
		set(value) {
			val modified = modified
			if (value != modified) {
				_modified = value
				dispatcher.onProjectModified(this, value)
			}
		}
	
	internal val uniqueID: Long
		get() {
			val v = data.nextID
			data.nextID = v + 1
			return v
		}
	
	public var openRocketVersion: String
		get() = data.openRocketVersion
		set(value) {
			val openRocketVersion = openRocketVersion
			if (value != openRocketVersion) {
				data.openRocketVersion = value
				dispatcher.onProjectOpenRocketVersionChanged(this, openRocketVersion, value)
			}
		}
	
	public val components: MutableList<Component> = ComponentList(app, this)
	
	public val configurations: MutableList<Configuration> = ConfigurationList(app, this)
	
	internal fun lookupComponent(id: Long): Component = componentMap.get(id)!!
	
	public fun load() {
		_modified = false
		componentMap.clear()
		file.inputStream().use {
			data = ProjectOuterClass.Project.parseFrom(it).toBuilder()
		}
		dispatcher.onProjectLoaded(this)
	}
	
	public fun save() {
		file.outputStream().use {
			data.build().writeTo(it)
		}
		dispatcher.onProjectSaved(this)
	}
}
