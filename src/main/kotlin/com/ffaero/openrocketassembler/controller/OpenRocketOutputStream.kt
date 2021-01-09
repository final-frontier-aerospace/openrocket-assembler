package com.ffaero.openrocketassembler.controller

import java.io.FilterOutputStream
import java.io.OutputStream
import java.util.zip.ZipOutputStream

class OpenRocketOutputStream private constructor (private val stream: ZipOutputStream) : FilterOutputStream(stream) {
	fun closeEntry() = stream.closeEntry()
	fun putNextEntry(entry: OpenRocketEntry) = stream.putNextEntry(entry)
	
	constructor(stream: OutputStream) : this(ZipOutputStream(stream)) {
	}
}
