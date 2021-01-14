package com.ffaero.openrocketassembler.controller.actions

import com.ffaero.openrocketassembler.controller.DispatcherBase
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class ActionRunner {
	companion object {
		private val log = LoggerFactory.getLogger(ActionRunner::class.java)
	}

	private val queue = ActionQueue()
	private var run = true
	private val lock = ReentrantLock()
	private val cond = lock.newCondition()
	
	private val thread = Thread(object : Runnable {
		override fun run() {
			log.info("ActionRunner thread starting up")
			lock.lock()
			try {
				while (true) {
					var task: ActionTask<*>? = null
					var diff: Long = 0
					while (run && run {
								task = queue.peek()
								diff = (task?.time ?: Long.MAX_VALUE) - System.currentTimeMillis()
								diff
							} > 0) {
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
						log.error("Task encountered exception", ex)
					} finally {
						lock.lock()
					}
				}
			} catch (ex: Exception) {
				log.error("ActionRunner thread encountered exception", ex)
			} finally {
				lock.unlock()
			}
			log.info("ActionRunner thread shutting down")
		}
	}, "ActionRunner").apply {
		start()
	}
	
	fun <TController : DispatcherBase<*, *>> enqueue(action: ActionBase<TController>, controller: TController, time: Long) {
		lock.lock()
		try {
			queue.add(action, controller, time)
			cond.signalAll()
		} finally {
			lock.unlock()
		}
	}
	
	fun <TController : DispatcherBase<*, *>> dequeue(action: ActionBase<TController>, controller: TController) {
		lock.lock()
		try {
			queue.remove(action, controller)
			cond.signalAll()
		} finally {
			lock.unlock()
		}
	}
	
	fun stop() {
		log.info("Requesting ActionRunner thread stop")
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
