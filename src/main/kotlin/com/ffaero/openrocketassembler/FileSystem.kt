package com.ffaero.openrocketassembler

import net.harawata.appdirs.AppDirsFactory
import java.io.File

object FileSystem {
	private const val publisher = "ffaero"
	private const val application = "openrocketassembler"
	private const val version = "1.0"
	private val appDirs = AppDirsFactory.getInstance()
	private val cacheDir = File(appDirs.getUserCacheDir(application, version, publisher)).apply { mkdirs() }
	private val tempDir = File(File(appDirs.getUserDataDir(application, version, publisher)), "Temp").apply { mkdirs() }
	
	fun getCacheFile(name: String) = File(cacheDir, name)
	fun getTempFile(owner: Any, name: String) = File(tempDir, String.format("%08X-%s", owner.hashCode(), name))
}
