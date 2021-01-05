package com.ffaero.openrocketassembler.controller

open class OpenRocketAdapter : OpenRocketListener {
	override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) = Unit
}
