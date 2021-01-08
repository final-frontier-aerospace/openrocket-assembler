package com.ffaero.openrocketassembler.controller

import java.io.FilterOutputStream
import java.io.OutputStream
import java.util.zip.ZipOutputStream

class OpenRocketOutputStream private constructor (private val stream: ZipOutputStream) : FilterOutputStream(stream) {
	public fun closeEntry() = stream.closeEntry()
	public fun finish() = stream.finish()
	public fun putNextEntry(entry: OpenRocketEntry) = stream.putNextEntry(entry)
	
	public constructor(stream: OutputStream) : this(ZipOutputStream(stream)) {
	}
}
