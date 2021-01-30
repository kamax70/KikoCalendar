package com.kiko.calendar.reservation

import com.kiko.calendar.landlord.LandlordDao
import com.kiko.calendar.reservation.event.ReservationCanceledEvent
import com.kiko.calendar.reservation.event.ReservationCreatedEvent
import com.kiko.calendar.reservation.ReservationStatus.*
import com.kiko.calendar.reservation.event.ReservationApprovedEvent
import internal.core.database.Transactor
import internal.core.eventbus.EventManager
import internal.core.exception.Exceptions.notFound
import org.jvnet.hk2.annotations.Service
import java.time.LocalDate
import javax.inject.Inject

@Service
class ReservationService @Inject constructor(
    private val landlordDao: LandlordDao,
    private val reservationDao: ReservationDao,
    private val transactor: Transactor,
    private val eventManager: EventManager
) {

    fun createReservation(date: LocalDate, period: Int, apartmentId: String, tenantId: String) {
        transactor.inTransaction {
            val landlord = landlordDao.findLandlordByApartmentId(apartmentId)
                ?: throw notFound("Apartment with id '$apartmentId' not found.")

            reservationDao.createReservation(
                date = date,
                period = period,
                apartmentId = apartmentId,
                tenantId = tenantId
            )

            eventManager.notify(
                ReservationCreatedEvent(
                    date = date,
                    period = period,
                    apartmentId = apartmentId,
                    landlordId = landlord.landlordId
                )
            )
        }
    }

    fun approveReservation(reservation: ReservationDto) =
        transactor.inTransaction {
            reservationDao.changeStatus(reservation, APPROVED)

            eventManager.notify(
                ReservationApprovedEvent(
                    date = reservation.date,
                    period = reservation.period,
                    apartmentId = reservation.apartmentId,
                    tenantId = reservation.tenantId
                )
            )
        }

    fun cancelReservationByLandlord(reservation: ReservationDto) =
        cancelReservation(reservation, CANCELED_BY_LANDLORD)

    fun cancelReservationByTenant(reservation: ReservationDto) =
        cancelReservation(reservation, CANCELED_BY_TENANT)

    private fun cancelReservation(reservation: ReservationDto, newStatus: ReservationStatus) {
        transactor.inTransaction {
            val landlord = landlordDao.findLandlordByApartmentId(reservation.apartmentId)
                ?: throw notFound("Apartment with id '${reservation.apartmentId}' not found.")

            reservationDao.changeStatus(reservation, newStatus)

            eventManager.notify(
                ReservationCanceledEvent(
                    date = reservation.date,
                    period = reservation.period,
                    apartmentId = reservation.apartmentId,
                    tenantId = reservation.tenantId,
                    landlordId = landlord.landlordId,
                    status = newStatus
                )
            )
        }
    }

    fun getReservation(apartmentId: String, date: LocalDate, period: Int) =
        transactor.inReadOnlyTransaction {
            reservationDao.getReservation(apartmentId = apartmentId, date = date, period = period)
        }

    fun getReservationsForApartment(apartmentId: String): Collection<ReservationDto> =
        transactor.inReadOnlyTransaction {
            reservationDao.getReservationsForApartment(apartmentId)
        }

    fun getReservationsForTenant(tenantId: String): Collection<ReservationDto> =
        transactor.inReadOnlyTransaction {
            reservationDao.getReservationsForTenant(tenantId)
        }

    fun getAll(): List<ReservationDto> =
        transactor.inReadOnlyTransaction {
            reservationDao.getAll()
        }
}