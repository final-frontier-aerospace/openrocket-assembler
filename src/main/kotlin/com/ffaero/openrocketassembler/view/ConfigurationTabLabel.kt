package com.ffaero.openrocketassembler.view

class ConfigurationTabLabel(private val tabs: ConfigurationTabView, title: String) : ConfigurationTabLabelBase(RocketList(tabs)) {
	init {
		text = title
		addMouseListener(tabs.tabMouseListener)
	}
}
