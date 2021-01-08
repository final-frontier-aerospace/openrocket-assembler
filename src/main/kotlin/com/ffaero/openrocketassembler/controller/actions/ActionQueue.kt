package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase
import java.util.PriorityQueue

class ActionQueue {
	private val queue = PriorityQueue<ActionTask<*>>()
	
	public fun <TController : DispatcherBase<*, *>> add(action: ActionBase<TController>, controller: TController, time: Long) {
		val ent = ActionTask<TController>(action, controller, time)
		queue.remove(ent)
		queue.add(ent)
	}
	
	public fun <TController : DispatcherBase<*, *>> remove(action: ActionBase<TController>, controller: TController) = queue.remove(ActionTask<TController>(action, controller, 0))
	public fun remove(e: ActionTask<*>) = queue.remove(e)
	public fun peek() = queue.peek()
}
