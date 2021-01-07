package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.ProjectController

class ConfigurationTabLabel(private val tabs: ConfigurationTabView, title: String) : ConfigurationTabLabelBase(RocketList(tabs.proj)) {
	init {
		text = title
		addMouseListener(tabs.tabMouseListener)
	}
}
