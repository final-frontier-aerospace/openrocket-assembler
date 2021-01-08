package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ApplicationAdapter
import com.ffaero.openrocketassembler.controller.ApplicationController
import com.ffaero.openrocketassembler.controller.ProjectController
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.EventQueue
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.JScrollPane
import javax.swing.JSplitPane

class EditorPanel(public val app: ApplicationView, private val proj: ProjectController) : JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {
	companion object {
		private const val maxRoundingErrorPx = 10
	}
	
	private fun targetDividerLocPx(split: Float) = ((getWidth() - getDividerSize()) * (split / 2 + 0.5f).coerceIn(0f, 1f)).toInt()
	private fun currentDividerLocPs(loc: Int) = loc / (getWidth() - getDividerSize()).toFloat() * 2 - 1
	
	private val appListener = object : ApplicationAdapter() {
		override fun onWindowSplitChanged(sender: ApplicationController, split: Float) = EventQueue.invokeLater {
			setSplit(split)
		}
	}
	
	private fun setSplit(split: Float) = setDividerLocation(targetDividerLocPx(split))
	
	private val left = JScrollPane(ComponentList(this, proj.components), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER).apply {
		setMinimumSize(Dimension(200, 200))
		setLeftComponent(this)
	}
	
	internal val configView = ConfigurationTabView(proj).apply {
		setMinimumSize(Dimension(200, 200))
		setRightComponent(this)
	}
	
	init {
		setOneTouchExpandable(true)
		addComponentListener(object : ComponentAdapter() {
			override fun componentResized(e: ComponentEvent?) = setSplit(proj.app.windowSplit)
		})
		addPropertyChangeListener(DIVIDER_LOCATION_PROPERTY, object : PropertyChangeListener {
			override fun propertyChange(evt: PropertyChangeEvent?) {
				if (evt == null) {
					return
				}
				val old = evt.getOldValue()
				val new = evt.getNewValue()
				if (!(old is Int) || !(new is Int)) {
					return
				}
				val target = targetDividerLocPx(proj.app.windowSplit)
				if (Math.abs(target - old) < maxRoundingErrorPx && Math.abs(target - new) > maxRoundingErrorPx) {
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
