package com.ffaero.openrocketassembler.view

import java.awt.Component
import javax.swing.JLabel

open class ConfigurationTabLabelBase(val view: Component) : JLabel() {
	private val unsafeMouseListener = MouseHierarchyTrampoline()

	private var _enableUnsafeUI = false
	var enableUnsafeUI: Boolean
			get() = _enableUnsafeUI
			set(value) {
				if (_enableUnsafeUI != value) {
					_enableUnsafeUI = value
					if (value) {
						unsafeMouseListener.enable()
						addMouseListener(unsafeMouseListener)
					} else {
						removeMouseListener(unsafeMouseListener)
					}
				}
			}

	fun insert(tabs: ConfigurationTabView, idx: Int) {
		tabs.insertTab("", null, view, "", idx)
		tabs.setTabComponentAt(idx, this)
	}
}
