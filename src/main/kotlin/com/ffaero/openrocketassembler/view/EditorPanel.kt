package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.controller.ProjectController
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import kotlin.math.abs

class EditorPanel(val app: ApplicationView, private val proj: ProjectController) : JSplitPane(HORIZONTAL_SPLIT) {
	companion object {
		private val log = LoggerFactory.getLogger(EditorPanel::class.java)
		private const val maxRoundingErrorPx = 10
	}
	
	private fun targetDividerLocPx(split: Float) = ((width - getDividerSize()) * (split / 2 + 0.5f).coerceIn(0f, 1f)).toInt()
	private fun currentDividerLocPs(loc: Int) = loc / (width - getDividerSize()).toFloat() * 2 - 1
	
	private val appListener = object : ApplicationAdapter() {
		override fun onWindowSplitChanged(sender: ApplicationController, split: Float) = EventQueue.invokeLater {
			setSplit(split)
		}
	}
	
	private fun setSplit(split: Float) {
		dividerLocation = targetDividerLocPx(split)
	}

	init {
		JScrollPane(ComponentList(this, proj.components), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER).apply {
			minimumSize = Dimension(200, 200)
			setLeftComponent(this)
		}
	}
	
	internal val configView = ConfigurationTabView(proj).apply {
		minimumSize = Dimension(200, 200)
		setRightComponent(this)
	}
	
	init {
		isOneTouchExpandable = true
		addComponentListener(object : ComponentAdapter() {
			override fun componentResized(e: ComponentEvent?) = setSplit(proj.app.windowSplit)
		})
		addPropertyChangeListener(DIVIDER_LOCATION_PROPERTY, object : PropertyChangeListener {
			override fun propertyChange(evt: PropertyChangeEvent?) {
				if (evt == null) {
					log.warn("Null PropertyChangeEvent")
					return
				}
				val old = evt.oldValue
				val new = evt.newValue
				if (old !is Int || new !is Int) {
					log.warn("Property value not int: {}, {}", old, new)
					return
				}
				val target = targetDividerLocPx(proj.app.windowSplit)
				if (abs(target - old) < maxRoundingErrorPx && abs(target - new) > maxRoundingErrorPx) {
					proj.app.windowSplit = currentDividerLocPs(new)
				}
			}
		})
		addHierarchyListener(object : ListenerLifecycleManager() {
			override fun addListeners() {
				appListener.onWindowSplitChanged(proj.app, proj.app.windowSplit)
				proj.app.addListener(appListener)
			}

			override fun removeListeners() {
				proj.app.removeListener(appListener)
			}
		})
	}
}
