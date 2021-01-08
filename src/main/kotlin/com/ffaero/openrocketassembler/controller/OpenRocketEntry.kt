package com.ffaero.openrocketassembler.controller

import java.util.zip.ZipEntry

class OpenRocketEntry(copy: ZipEntry): ZipEntry(copy) {
	companion object {
		public val rocketXMLEntry: OpenRocketEntry
				get() = OpenRocketEntry(ZipEntry("rocket.ork"))
	}
	
	public val isRocketXML: Boolean
			get() = getName().equals(rocketXMLEntry.getName())
}
