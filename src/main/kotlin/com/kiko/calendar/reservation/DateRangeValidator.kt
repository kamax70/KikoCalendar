package com.kiko.calendar.reservation

import internal.core.exception.Exceptions.invalidArgument
import internal.core.logging.logger
import org.jvnet.hk2.annotations.Service
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@Service
class DateRangeValidator @Inject constructor(
    private val clock: Clock
) {

    companion object {
        private val logger = logger()
        const val MAX_20_MIN_PERIODS_COUNT_IN_DAY: Long = 33
    }

    fun isValid(date: LocalDate, period: Int): Boolean {
        // 10 am each day plus 'period' number multiply 20 min interval
        if (period < 0 || period > MAX_20_MIN_PERIODS_COUNT_IN_DAY) {
            throw invalidArgument("Invalid period '$period'.")
        }
        val startDateTime = date.atTime(10, 0).plusMinutes(20L * period)
        val now = LocalDateTime.now(clock)
        logger.debug("Reservation time = $startDateTime, current time = $now")
        return !startDateTime.isBefore(now.plusDays(1))
                && !startDateTime.isAfter(now.plusDays(7))
    }
}