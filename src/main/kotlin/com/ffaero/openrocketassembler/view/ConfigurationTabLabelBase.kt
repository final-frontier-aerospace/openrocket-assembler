package com.ffaero.openrocketassembler.view

import java.awt.Component
import javax.swing.JLabel

open class ConfigurationTabLabelBase(public val view: Component) : JLabel() {
	public fun insert(tabs: ConfigurationTabView, idx: Int) {
		tabs.insertTab("", null, view, "", idx)
		tabs.setTabComponentAt(idx, this)
		addMouseListener(MouseHierarchyTrampoline())
	}
}
