package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase

class ActionFactory {
	private val runner = ActionRunner()

	private val openRocketDownloader = OpenRocketDownloader()
	val openRocketUpdateCheck = OpenRocketUpdateCheck()
	
	val projectActions = mk(
		openRocketDownloader
	)
	
	val applicationActions = mk(
		openRocketUpdateCheck
	)
	
	fun <TController: DispatcherBase<*, *>> addListeners(arr: Array<out ActionBase<TController>>, controller: TController) = arr.forEach { it.doAddListeners(controller) }
	fun <TController: DispatcherBase<*, *>> removeListeners(arr: Array<out ActionBase<TController>>, controller: TController) = arr.forEach { it.doRemoveListeners(controller) }
	fun stop() = runner.stop()
	
	private fun <TController: DispatcherBase<*, *>> mk(vararg arr: ActionBase<TController>): Array<out ActionBase<TController>> {
		arr.forEach {
			it.attach(runner)
		}
		return arr
	}
}
