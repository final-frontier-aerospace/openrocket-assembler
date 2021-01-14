package com.ffaero.openrocketassembler.controller

import org.apache.logging.log4j.core.*
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory

@Plugin(name = "LogAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
class LogAppender(name: String, filter: Filter?, layout: Layout<*>, ignoreExceptions: Boolean, properties: Array<Property>?) : AbstractAppender(name, filter, layout, ignoreExceptions, properties) {
    companion object {
        private val listeners = object : ListenerListBase<LogAppenderTarget>() {}

        @JvmStatic
        @PluginFactory
        fun createAppender(@PluginAttribute("name") name: String, @PluginAttribute("ignoreExceptions") ignoreExceptions: Boolean, @PluginElement("Layout") layout: Layout<*>, @PluginElement("Filters") filter: Filter?): LogAppender {
            return LogAppender(name, filter, layout, ignoreExceptions, emptyArray())
        }

        fun addTarget(target: LogAppenderTarget) = listeners.add(target)
        fun removeTarget(target: LogAppenderTarget) = listeners.remove(target)
    }

    override fun append(event: LogEvent?) {
        if (listeners.isNotEmpty() && event != null) {
            val buf = layout.toByteArray(event)
            listeners.forEach { it.onLogEntry(buf) }
        }
    }
}
