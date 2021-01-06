package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase

abstract class ActionBase<TController : DispatcherBase<*, *>> {
	private var runner: ActionRunner? = null
	private val controllers = HashSet<TController>()
	
	public abstract fun runAction(controller: TController)
	protected abstract fun addListeners(controller: TController)
	protected abstract fun removeListeners(controller: TController)
	
	internal fun doAddListeners(controller: TController) {
		controllers.add(controller)
		addListeners(controller)
	}
	
	internal fun doRemoveListeners(controller: TController) {
		removeListeners(controller)
		controllers.remove(controller)
	}
	
	internal fun attach(runner: ActionRunner) {
		this.runner = runner
	}
	
	protected fun enqueueAction(controller: TController, time: Long = 0) {
		runner?.enqueue(this, controller, time)
	}
	
	protected fun enqueueActionAll(time: Long = 0) {
		if (runner != null) {
			controllers.forEach {
				runner!!.enqueue(this, it, time)
			}
		}
	}
	
	protected fun dequeueAction(controller: TController) {
		runner?.dequeue(this, controller)
	}
	
	protected fun dequeueActionAll() {
		if (runner != null) {
			controllers.forEach {
				runner!!.dequeue(this, it)
			}
		}
	}
}
