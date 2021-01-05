package com.ffaero.openrocketassembler.controller

class PeriodicRunner {
	private val tasks = ArrayList<PeriodicTask>()
	private var run = true
	private val thread = Thread(object : Runnable {
		override fun run() {
			while (run) {
				try {
					var nextTime = Long.MAX_VALUE
					var nextTask: PeriodicTask? = null
					tasks.forEach {
						if (it.period > 0) {
							val time = it.lastRun + it.period
							if (time < nextTime) {
								nextTime = time
								nextTask = it
							}
						}
					}
					if (nextTask == null) {
						Thread.sleep(Long.MAX_VALUE)
					} else {
						val diff = nextTime - System.currentTimeMillis()
						if (diff > 0) {
							Thread.sleep(diff)
						}
						val timeBefore = nextTask!!.lastRun
						nextTask!!.task.run()
						if (timeBefore == nextTask!!.lastRun) {
							nextTask!!.lastRun = System.currentTimeMillis()
						}
					}
				} catch (ex: InterruptedException) {
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
	}).apply {
		start()
	}
	
	public fun addTask(task: PeriodicTask) {
		tasks.add(task)
		task.addListener(object : PeriodicTaskAdapter() {
			override fun onRun(sender: PeriodicTask) = thread.interrupt()
			override fun onPeriodChange(sender: PeriodicTask) = thread.interrupt()
		})
		thread.interrupt()
	}
	
	public fun stop() {
		run = false
		thread.interrupt()
		thread.join()
	}
}
