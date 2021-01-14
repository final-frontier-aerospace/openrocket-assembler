package com.ffaero.openrocketassembler.controller

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class LogController(private val app: ApplicationController) {
    companion object {
        private val log = LoggerFactory.getLogger(LogController::class.java)
    }

    private val lock = ReentrantLock()
    private val buffer = LinkedList<ByteArray>()
    private var stream: FileOutputStream? = null
    private var _file: File? = null
    private val filename = String.format("OpenRocket Assembler - %s.log", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").format(LocalDateTime.now()))

    val file: File?
            get() = _file

    private val logTarget = object : LogAppenderTarget {
        override fun onLogEntry(entry: ByteArray) {
            lock.lock()
            try {
                val stream = stream
                if (stream != null) {
                    try {
                        stream.write(entry)
                        return
                    } catch (ex: IOException) {
                        stream.close()
                        this@LogController.stream = null
                        log.error("Unable to write log entry", ex)
                    }
                }
                buffer.offer(entry.clone())
            } finally {
                lock.unlock()
            }
        }
    }

    private val settingsListener = object : SettingAdapter() {
        private var last = ""

        override fun onSettingsUpdated(sender: SettingController) {
            val tempDir = sender.tempDir
            if (tempDir.absolutePath != last) {
                last = tempDir.absolutePath
                lock.lock()
                try {
                    stream?.close()
                    stream = null
                    val oldFile = file
                    val newFile = File(tempDir, filename)
                    if (newFile.exists()) {
                        newFile.delete()
                    }
                    if (oldFile?.exists() == true) {
                        oldFile.renameTo(newFile)
                    }
                    _file = newFile
                    val stream = FileOutputStream(newFile, true)
                    this@LogController.stream = stream
                    while (buffer.isNotEmpty()) {
                        stream.write(buffer.peek())
                        buffer.pop()
                    }
                } catch (ex: IOException) {
                    stream?.close()
                    stream = null
                    log.error("Unable to open log file", ex)
                } finally {
                    lock.unlock()
                }
            }
        }
    }

    init {
        LogAppender.addTarget(logTarget)
    }

    internal fun initSettings() {
        settingsListener.onSettingsUpdated(app.settings)
        app.settings.addListener(settingsListener)
    }

    internal fun stop() {
        LogAppender.removeTarget(logTarget)
        app.settings.removeListener(settingsListener)
        lock.lock()
        try {
            stream?.close()
            stream = null
            val file = file
            if (file?.exists() == true && !app.settings.keepLogFiles) {
                file.delete()
            }
        } finally {
            lock.unlock()
        }
    }
}
