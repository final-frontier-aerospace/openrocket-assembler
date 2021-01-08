package com.ffaero.openrocketassembler.controller

import java.io.OutputStream
import java.io.FilterOutputStream

class UncloseableOutputStream(stream: OutputStream) : FilterOutputStream(stream) {
	override fun close() = Unit
}
