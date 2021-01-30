package com.kiko.calendar.tenant

import com.kiko.calendar.reservation.DateRangeValidator
import com.kiko.calendar.reservation.ReservationService
import com.kiko.calendar.reservation.ReservationStatus.*
import internal.core.database.Transactor
import internal.core.exception.Exceptions.notFound
import internal.core.exception.Exceptions.validationException
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class TenantService @Inject constructor(
    private val reservationService: ReservationService,
    private val dateRangeValidator: DateRangeValidator,
    private val transactor: Transactor
) {

    fun createReservation(tenantId: String, date: LocalDate, period: Int, apartmentId: String) {
        if (!dateRangeValidator.isValid(date = date, period = period)) {
            throw validationException("Reservation for date=[$date], period=[$period] is forbidden.")
        }
        transactor.inTransaction {
            validateCreateReservation(
                tenantId = tenantId,
                date = date,
                period = period,
                apartmentId = apartmentId
            )
            reservationService.createReservation(
                tenantId = tenantId,
                date = date,
                period = period,
                apartmentId = apartmentId
            )
        }
    }

    fun cancelReservation(tenantId: String, date: LocalDate, period: Int, apartmentId: String) {
        transactor.inTransaction {
            val reservation = reservationService.getReservation(apartmentId = apartmentId, date = date, period = period)
                ?: throw notFound("Reservation for date=[$date], period=[$period] doesn't exist.")
            if (reservation.tenantId != tenantId) {
                throw validationException("Tenant has no permissions to cancel that reservation.")
            }
            when (reservation.status) {
                NEW, APPROVED -> reservationService.cancelReservationByTenant(reservation)
                CANCELED_BY_LANDLORD, CANCELED_BY_TENANT -> throw validationException("Reservation is already canceled.")
            }
        }
    }

    private fun validateCreateReservation(tenantId: String, date: LocalDate, period: Int, apartmentId: String) {
        val apartmentReservations = reservationService.getReservationsForApartment(apartmentId)
        apartmentReservations.asSequence()
            .filter { it.tenantId == tenantId && it.status != CANCELED_BY_LANDLORD && it.status != CANCELED_BY_TENANT }
            .firstOrNull()
            ?.let {
                throw validationException("Tenant has already reserved that apartment.")
            }
        apartmentReservations.asSequence()
            .filter { it.date == date && it.period == period && it.status == CANCELED_BY_LANDLORD }
            .firstOrNull()
            ?.let {
                throw validationException("Apartment can't be reserved at that time because landlord is busy.")
            }
        apartmentReservations.asSequence()
            .filter { it.date == date && it.period == period && it.status != CANCELED_BY_TENANT }
            .firstOrNull()
            ?.let {
                throw validationException("Apartment is already reserved for that time by another tenant.")
            }

        val tenantReservations = reservationService.getReservationsForTenant(tenantId)
        tenantReservations.asSequence()
            .filter { it.date == date && it.period == period && it.apartmentId != apartmentId && it.status != CANCELED_BY_LANDLORD && it.status != CANCELED_BY_TENANT }
            .firstOrNull()
            ?.let {
                throw validationException("Tenant has another reserved apartment at that time.")
            }
    }
}