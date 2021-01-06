package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase

class ActionTask<TController : DispatcherBase<*, *>>(public val action: ActionBase<TController>, public val controller: TController, public val time: Long) : Comparable<ActionTask<*>> {
	public fun run() {
		action.runAction(controller)
	}
	
	override fun compareTo(other: ActionTask<*>): Int = time.compareTo(other.time)
	
	override fun equals(other: Any?): Boolean {
		if (other == null) {
			return false
		}
		if (!(other is ActionTask<*>)) {
			return false
		}
		return action == other.action && controller == other.controller
	}

	override fun hashCode(): Int {
		var hash = 7
		hash = 53 * hash + action.hashCode()
		hash = 53 * hash + controller.hashCode()
		return hash
	}
}
