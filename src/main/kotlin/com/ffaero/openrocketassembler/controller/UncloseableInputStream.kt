package com.ffaero.openrocketassembler.controller

import java.io.InputStream
import java.io.FilterInputStream

class UncloseableInputStream(stream: InputStream) : FilterInputStream(stream) {
	override fun close() = Unit
}
