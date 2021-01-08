package com.ffaero.openrocketassembler.controller

import java.io.FilterInputStream
import java.io.InputStream

class UncloseableInputStream(stream: InputStream) : FilterInputStream(stream) {
	override fun close() = Unit
}
