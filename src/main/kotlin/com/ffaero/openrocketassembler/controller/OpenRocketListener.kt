package com.ffaero.openrocketassembler.controller

interface OpenRocketListener {
	fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>)
}
