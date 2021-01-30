package com.kiko.calendar

import com.kiko.calendar.reservation.ReservationDto
import com.kiko.calendar.reservation.ReservationStatus
import java.time.LocalDate

object Fixtures {

    val date = LocalDate.parse("2020-01-01")
    const val period = 5
    const val apartmentId = "apartmentId"
    const val tenantId = "tenantId"
    const val landlordId = "landlordId"

    val reservation = ReservationDto(
        date = date,
        period = period,
        apartmentId = apartmentId,
        tenantId = tenantId,
        status = ReservationStatus.NEW
    )
}