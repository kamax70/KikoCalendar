package com.kiko.calendar.reservation

import java.time.LocalDate

data class ReservationDto(
    val date: LocalDate,
    val period: Int,
    val apartmentId: String,
    val tenantId: String,
    val status: ReservationStatus
)

data class ReservationKey(
    val apartmentId: String,
    val date: LocalDate,
    val period: Int
)

fun ReservationDto.reservationKey() =
    ReservationKey(
        apartmentId = apartmentId,
        date = date,
        period = period
    )

enum class ReservationStatus {
    NEW, APPROVED, CANCELED_BY_LANDLORD, CANCELED_BY_TENANT
}