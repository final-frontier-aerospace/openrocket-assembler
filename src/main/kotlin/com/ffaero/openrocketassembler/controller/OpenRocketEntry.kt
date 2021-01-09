package com.ffaero.openrocketassembler.controller

import java.util.zip.ZipEntry

class OpenRocketEntry(copy: ZipEntry): ZipEntry(copy) {
	companion object {
		val rocketXMLEntry: OpenRocketEntry
				get() = OpenRocketEntry(ZipEntry("rocket.ork"))
	}
	
	val isRocketXML: Boolean
			get() = name == rocketXMLEntry.name
}
