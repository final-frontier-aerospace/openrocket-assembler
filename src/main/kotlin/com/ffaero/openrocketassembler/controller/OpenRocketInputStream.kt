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
			return if (arr.contentEquals(byteArrayOf(0x50, 0x4B, 0x03, 0x04))) {
				ZipInputStream(buf)
			} else {
				GZIPInputStream(buf)
			}
		}
	}
	
	private var gotEntry = false
	
	fun closeEntry() {
		val s = `in`
		if (s is ZipInputStream) {
			s.closeEntry()
		} else {
			s.close()
		}
	}
	
	fun getNextEntry(): OpenRocketEntry? {
		val s = `in`
		return if (s is ZipInputStream) {
			val entry = s.nextEntry
			if (entry == null) {
				null
			} else {
				OpenRocketEntry(entry)
			}
		} else if (gotEntry) {
			null
		} else {
			gotEntry = true
			OpenRocketEntry.rocketXMLEntry
		}
	}
}
