package com.ffaero.openrocketassembler.controller

class SettingListenerList : ListenerListBase<SettingListener>(), SettingListener {
    override fun onSettingsUpdated(sender: SettingController) = forEach { it.onSettingsUpdated(sender) }
}
