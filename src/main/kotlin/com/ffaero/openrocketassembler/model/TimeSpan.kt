package com.ffaero.openrocketassembler.model

class TimeSpan(private val title: String, val millis: Long) {
    companion object {
        private val hourly = TimeSpan("Hourly", 1000L * 60 * 60)
        private val daily = TimeSpan("Daily", 1000L * 60 * 60 * 24)
        private val weekly = TimeSpan("Weekly", 1000L * 60 * 60 * 24 * 7)
        private val monthly = TimeSpan("Monthly", 1000L * 60 * 60 * 24 * 7 * 30)
        private val yearly = TimeSpan("Yearly", 1000L * 60 * 60 * 24 * 7 * 365)
        private val manual = TimeSpan("Manual", Long.MAX_VALUE)

        val standard = arrayOf(hourly, daily, weekly, monthly, yearly, manual)
    }

    override fun toString(): String = title
}
