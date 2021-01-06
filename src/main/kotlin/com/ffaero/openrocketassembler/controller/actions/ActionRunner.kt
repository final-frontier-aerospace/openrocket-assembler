package com.ffaero.openrocketassembler.controller.actions

import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.TimeUnit
import com.ffaero.openrocketassembler.controller.DispatcherBase

class ActionRunner {
	companion object {
		public val isRunner = object : ThreadLocal<Boolean>() {
			override fun initialValue(): Boolean? = false
		}
	}
	
	private val queue = ActionQueue()
	private var run = true
	private val lock = ReentrantLock()
	private val cond = lock.newCondition()
	
	private val thread = Thread(object : Runnable {
		override fun run() {
			isRunner.set(true)
			lock.lock()
			try {
				while (true) {
					var task: ActionTask<*>? = null
					var diff: Long = 0
					while (run && {task = queue.peek(); diff = (task?.time ?: Long.MAX_VALUE) - System.currentTimeMillis(); diff}() > 0) {
						cond.await(diff, TimeUnit.MILLISECONDS)
					}
					if (!run) {
						return
					}
					queue.remove(task!!)
					lock.unlock()
					try {
						task!!.run()
					} catch (ex: Exception) {
						ex.printStackTrace()
					} finally {
						lock.lock()
					}
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			} finally {
				lock.unlock()
			}
		}
	}).apply {
		start()
	}
	
	public fun <TController : DispatcherBase<*, *>> enqueue(action: ActionBase<TController>, controller: TController, time: Long) {
		lock.lock()
		try {
			queue.add(action, controller, time)
			cond.signalAll()
		} finally {
			lock.unlock()
		}
	}
	
	public fun <TController : DispatcherBase<*, *>> dequeue(action: ActionBase<TController>, controller: TController) {
		lock.lock()
		try {
			queue.remove(action, controller)
			cond.signalAll()
		} finally {
			lock.unlock()
		}
	}
	
	public fun stop() {
		lock.lock()
		try {
			run = false
			cond.signalAll()
		} finally {
			lock.unlock()
		}
		thread.join()
	}
}
