package com.ffaero.openrocketassembler.view

import java.awt.EventQueue
import java.awt.Toolkit
import java.io.Closeable

class RedirectedEventQueue : EventQueue(), AutoCloseable, Closeable {
	override fun close() {
		pop()
	}
	
	init {
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(this)
	}
}
