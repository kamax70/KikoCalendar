package com.kiko.calendar.reservation

import internal.core.exception.BusinessValidationException
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.assertj.core.api.Assertions
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DateRangeValidatorTest {

    private val clock: Clock = Clock.fixed(Instant.parse("2020-01-01T10:01:00.00Z"), ZoneId.of("UTC"))
    private val validator = DateRangeValidator(clock)

    @Test
    fun `validate possible cases`() {
        assertTrue(validator.isValid(LocalDate.parse("2020-01-03"), 3))
        assertFalse(validator.isValid(LocalDate.parse("2019-01-03"), 3))
        assertFalse(validator.isValid(LocalDate.parse("2022-01-03"), 3))

        assertFalse(validator.isValid(LocalDate.parse("2020-01-02"), 0)) // false - 10.00 less than 10.01
        assertTrue(validator.isValid(LocalDate.parse("2020-01-02"), 1)) // true - 10.20 greater than 10.01

        assertTrue(validator.isValid(LocalDate.parse("2020-01-08"), 0))
        assertFalse(validator.isValid(LocalDate.parse("2020-01-08"), 1)) // false - more than 7 days from now
    }

    @Test
    fun `invalid period`() {
        (0..33).forEach { validator.isValid(LocalDate.parse("2020-01-03"), it) }

        Assertions.assertThatThrownBy {
            validator.isValid(LocalDate.parse("2020-01-03"), -1)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Invalid period '-1'.")

        Assertions.assertThatThrownBy {
            validator.isValid(LocalDate.parse("2020-01-03"), 34)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Invalid period '34'.")
    }
}