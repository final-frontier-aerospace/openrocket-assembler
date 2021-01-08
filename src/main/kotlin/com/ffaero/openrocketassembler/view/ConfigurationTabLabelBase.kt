package com.ffaero.openrocketassembler.view

import javax.swing.JLabel
import java.awt.Component

open class ConfigurationTabLabelBase(public val view: Component) : JLabel() {
	public fun insert(tabs: ConfigurationTabView, idx: Int) {
		tabs.insertTab("", null, view, "", idx)
		tabs.setTabComponentAt(idx, this)
	}
}
