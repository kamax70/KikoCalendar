package com.kiko.calendar.reservation.event

import com.kiko.calendar.reservation.ReservationStatus
import java.time.LocalDate

data class ReservationCreatedEvent(
    val date: LocalDate,
    val period: Int,
    val apartmentId: String,
    val landlordId: String,
)

data class ReservationCanceledEvent(
    val date: LocalDate,
    val period: Int,
    val apartmentId: String,
    val landlordId: String,
    val tenantId: String,
    val status: ReservationStatus
)

data class ReservationApprovedEvent(
    val date: LocalDate,
    val period: Int,
    val apartmentId: String,
    val tenantId: String
)