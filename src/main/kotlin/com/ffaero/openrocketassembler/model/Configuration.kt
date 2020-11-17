package com.ffaero.openrocketassembler.model

import com.ffaero.openrocketassembler.model.proto.ConfigurationOuterClass
import java.util.function.Consumer
import java.util.stream.Stream
import java.util.Spliterator
import java.nio.ByteBuffer
import com.google.protobuf.ByteString

class Configuration internal constructor(app: Application, private val proj: Project, internal val data: ConfigurationOuterClass.Configuration.Builder) {
	private class ComponentList(private val config: Configuration) : ModelList<Component>() {
		override val size: Int
			get() = config.data.getComponentIDsCount()

		override fun add(index: Int, element: Component) {
			config.data.getComponentIDsList().add(index, element.id)
			config.dispatcher.onConfigurationComponentAdded(config, element, index)
		}
	
		override fun get(index: Int): Component = config.proj.lookupComponent(config.data.getComponentIDs(index))
		
		override fun equals(other: Any?): Boolean {
			if (!(other is ComponentList)) {
				return false;
			}
			return config == other.config
		}
		
		override fun hashCode(): Int = config.hashCode()
		
		override fun indexOf(element: Component): Int = config.data.getComponentIDsList().indexOf(element.id)
		
		override fun lastIndexOf(element: Component): Int = config.data.getComponentIDsList().lastIndexOf(element.id)
		
		override fun removeAt(index: Int): Component {
			val v = config.proj.lookupComponent(config.data.getComponentIDsList().removeAt(index))
			config.dispatcher.onConfigurationComponentRemoved(config, v, index)
			return v
		}
	}
	
	private class SupportingFileList(private val config: Configuration) : ModelList<SupportingFile>() {
		override val size: Int
			get() = config.data.getFilesCount()

		override fun add(index: Int, element: SupportingFile) {
			config.data.getFilesList().add(index, element.data)
			config.dispatcher.onConfigurationSupportingFileAdded(config, element, index)
		}
	
		override fun get(index: Int): SupportingFile = SupportingFile(config.data.getFiles(index))
		
		override fun equals(other: Any?): Boolean {
			if (!(other is SupportingFileList)) {
				return false;
			}
			return config == other.config
		}
		
		override fun hashCode(): Int = config.hashCode()
		
		override fun indexOf(element: SupportingFile): Int = config.data.getFilesList().indexOf(element.data)
		
		override fun lastIndexOf(element: SupportingFile): Int = config.data.getFilesList().lastIndexOf(element.data)
		
		override fun removeAt(index: Int): SupportingFile {
			val v = SupportingFile(config.data.getFilesList().removeAt(index))
			config.dispatcher.onConfigurationSupportingFileRemoved(config, v, index)
			return v
		}
	}
	
	private val dispatcher: ApplicationListener = app.dispatcher
	
	public val id: Long
		get() = data.id
	
	public var name: String
		get() = data.name
		set(value) {
			val name = name
			if (value != name) {
				data.name = value
				dispatcher.onConfigurationRenamed(this, name, value)
			}
		}
	
	public val components: MutableList<Component> = ComponentList(this)
	
	public var templateRocketXML: ByteBuffer
		get() = data.templateRocketXML.asReadOnlyByteBuffer()
		set(value) {
			data.templateRocketXML = ByteString.copyFrom(value)
			dispatcher.onConfigurationTemplateRocketUpdated(this)
		}
	
	public var templateComponentXML: ByteBuffer
		get() = data.templateComponentXML.asReadOnlyByteBuffer()
		set(value) {
			data.templateComponentXML = ByteString.copyFrom(value)
			dispatcher.onConfigurationTemplateComponentUpdated(this)
		}
	
	public val supportingFiles: MutableList<SupportingFile> = SupportingFileList(this)
	
	override fun equals(other: Any?): Boolean {
		if (!(other is Configuration)) {
			return false
		}
		return proj == other.proj && data == other.data
	}

	override fun hashCode(): Int = 31 * proj.hashCode() + data.hashCode()
	
	internal constructor(app: Application, proj: Project) : this(app, proj, ConfigurationOuterClass.Configuration.newBuilder().apply {
		id = proj.uniqueID
		name = "Untitled Configuration"
		TODO("Set templateRocketXML")
		TODO("Set templateComponentXML")
	}) {
	}
}
