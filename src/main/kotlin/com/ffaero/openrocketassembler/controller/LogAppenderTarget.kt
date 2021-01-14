package com.ffaero.openrocketassembler.controller

interface LogAppenderTarget {
    fun onLogEntry(entry: ByteArray)
}
