package com.kiko.calendar.landlord

import com.kiko.calendar.reservation.ReservationService
import com.kiko.calendar.reservation.ReservationStatus.*
import internal.core.database.Transactor
import internal.core.exception.Exceptions.notFound
import internal.core.exception.Exceptions.validationException
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class LandlordService @Inject constructor(
    private val reservationService: ReservationService,
    private val transactor: Transactor
){

    fun approveReservation(date: LocalDate, period: Int, apartmentId: String) {
        transactor.inTransaction {
            val reservation = reservationService.getReservation(apartmentId = apartmentId, date = date, period = period)
                ?: throw notFound("Reservation for date=[$date], period=[$period], apartmentId=[$apartmentId] doesn't exist.")
            when (reservation.status) {
                NEW -> reservationService.approveReservation(reservation)
                CANCELED_BY_LANDLORD, CANCELED_BY_TENANT -> throw validationException("Reservation is canceled.")
                APPROVED -> throw validationException("Reservation is already approved.")
            }
        }
    }

    fun cancelReservation(date: LocalDate, period: Int, apartmentId: String) {
        transactor.inTransaction {
            val reservation = reservationService.getReservation(apartmentId = apartmentId, date = date, period = period)
                ?: throw notFound("Reservation for date=[$date], period=[$period], apartmentId=[$apartmentId] doesn't exist.")
            when (reservation.status) {
                NEW, APPROVED -> reservationService.cancelReservationByLandlord(reservation)
                CANCELED_BY_LANDLORD, CANCELED_BY_TENANT -> throw validationException("Reservation is already canceled.")
            }
        }
    }
}