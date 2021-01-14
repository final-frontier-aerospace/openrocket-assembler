package com.ffaero.openrocketassembler.crash

import org.slf4j.LoggerFactory
import java.io.File

object CrashSetup {
    private val log = LoggerFactory.getLogger(CrashSetup::class.java)
    private var setup = false

    fun setup(logFile: () -> File?) {
        if (!setup) {
            setup = true
        } else {
            return
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            log.error(String.format("Uncaught exception on thread %s (%d)", t.name, t.id), e)
            val file = logFile()
            if (file != null) {
                CrashReporter(file)
            }
        }
    }
}
