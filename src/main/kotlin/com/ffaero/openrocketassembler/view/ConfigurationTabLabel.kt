package com.ffaero.openrocketassembler.view

class ConfigurationTabLabel(tabs: ConfigurationTabView, title: String) : ConfigurationTabLabelBase(RocketList(tabs)) {
	init {
		text = title
		addMouseListener(tabs.tabMouseListener)
	}
}
