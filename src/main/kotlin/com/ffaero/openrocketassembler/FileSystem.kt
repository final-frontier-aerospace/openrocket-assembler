package com.ffaero.openrocketassembler

import java.io.File
import net.harawata.appdirs.AppDirsFactory

object FileSystem {
	private const val publisher = "ffaero"
	private const val application = "openrocketassembler"
	private const val version = "1.0"
	private val appDirs = AppDirsFactory.getInstance()
	private val cacheDir = File(appDirs.getUserCacheDir(application, version, publisher)).apply { mkdirs() }
	
	public fun getCacheFile(name: String): File {
		return File(cacheDir, name)
	}
}
