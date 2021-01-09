package com.ffaero.openrocketassembler.view

import java.awt.Component
import javax.swing.JLabel

open class ConfigurationTabLabelBase(val view: Component) : JLabel() {
	fun insert(tabs: ConfigurationTabView, idx: Int) {
		tabs.insertTab("", null, view, "", idx)
		tabs.setTabComponentAt(idx, this)
		addMouseListener(MouseHierarchyTrampoline())
	}
}
