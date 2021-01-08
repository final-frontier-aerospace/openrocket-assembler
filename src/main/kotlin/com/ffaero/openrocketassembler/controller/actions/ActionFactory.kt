package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase

class ActionFactory {
	private val runner = ActionRunner()
	
	public val defaultOpenRocketVersion = DefaultOpenRocketVersion()
	public val openRocketDownloader = OpenRocketDownloader()
	public val openRocketUpdateCheck = OpenRocketUpdateCheck()
	
	public val projectActions = mk(
		defaultOpenRocketVersion,
		openRocketDownloader
	)
	
	public val applicationActions = mk(
		openRocketUpdateCheck
	)
	
	public fun <TController: DispatcherBase<*, *>> addListeners(arr: Array<out ActionBase<TController>>, controller: TController) = arr.forEach { it.doAddListeners(controller) }
	public fun <TController: DispatcherBase<*, *>> removeListeners(arr: Array<out ActionBase<TController>>, controller: TController) = arr.forEach { it.doRemoveListeners(controller) }
	public fun stop() = runner.stop()
	
	private fun <TController: DispatcherBase<*, *>> mk(vararg arr: ActionBase<TController>): Array<out ActionBase<TController>> {
		arr.forEach {
			it.attach(runner)
		}
		return arr
	}
}
