package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase
import java.util.*

class ActionQueue {
	private val queue = PriorityQueue<ActionTask<*>>()
	
	fun <TController : DispatcherBase<*, *>> add(action: ActionBase<TController>, controller: TController, time: Long) {
		val ent = ActionTask(action, controller, time)
		queue.remove(ent)
		queue.add(ent)
	}
	
	fun <TController : DispatcherBase<*, *>> remove(action: ActionBase<TController>, controller: TController) = queue.remove(ActionTask(action, controller, 0))
	fun remove(e: ActionTask<*>) = queue.remove(e)
	fun peek(): ActionTask<*>? = queue.peek()
}
