package com.ffaero.openrocketassembler.controller

import java.io.FilterOutputStream
import java.io.OutputStream

class UncloseableOutputStream(stream: OutputStream) : FilterOutputStream(stream) {
	override fun close() = Unit
}
