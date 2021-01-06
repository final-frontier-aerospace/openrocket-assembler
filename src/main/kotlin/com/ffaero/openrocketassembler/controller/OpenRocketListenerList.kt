package com.ffaero.openrocketassembler.controller

class OpenRocketListenerList : ListenerListBase<OpenRocketListener>(), OpenRocketListener {
	override fun onOpenRocketVersionsUpdated(sender: OpenRocketController, versions: List<String>) = forEach { it.onOpenRocketVersionsUpdated(sender, versions) }
}
