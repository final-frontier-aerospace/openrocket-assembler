package com.ffaero.openrocketassembler.controller

import java.io.BufferedInputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream

class OpenRocketInputStream(stream: InputStream) : FilterInputStream(encapsulate(stream)) {
	companion object {
		private fun encapsulate(stream: InputStream): InputStream {
			val buf = BufferedInputStream(stream)
			buf.mark(4)
			val arr = ByteArray(4)
			buf.read(arr)
			buf.reset()
			if (arr.contentEquals(byteArrayOf(0x50, 0x4B, 0x03, 0x04))) {
				return ZipInputStream(buf)
			} else {
				return GZIPInputStream(buf)
			}
		}
	}
	
	private var gotEntry = false
	
	public fun closeEntry() {
		val s = `in`
		if (s is ZipInputStream) {
			s.closeEntry()
		} else {
			s.close()
		}
	}
	
	public fun getNextEntry(): OpenRocketEntry? {
		val s = `in`
		if (s is ZipInputStream) {
			val entry = s.getNextEntry()
			if (entry == null) {
				return null
			} else {
				return OpenRocketEntry(entry)
			}
		} else if (gotEntry) {
			return null
		} else {
			gotEntry = true
			return OpenRocketEntry.rocketXMLEntry
		}
	}
}
