package com.kiko.calendar.reservation

import org.jvnet.hk2.annotations.Service
import java.time.LocalDate

@Service
class ReservationDao {

    private val reservationKeyToReservationMap = HashMap<ReservationKey, ReservationDto>()
    private val apartmentIdToReservationMap = HashMap<String, MutableCollection<ReservationKey>>()
    private val tenantIdToReservationMap = HashMap<String, MutableCollection<ReservationKey>>()

    fun createReservation(date: LocalDate, period: Int, apartmentId: String, tenantId: String) {
        val reservation = ReservationDto(
            date = date,
            period = period,
            apartmentId = apartmentId,
            tenantId = tenantId,
            status = ReservationStatus.NEW
        )

        reservationKeyToReservationMap[ReservationKey(apartmentId, date, period)] = reservation
        apartmentIdToReservationMap.computeIfAbsent(apartmentId) { HashSet() }.add(reservation.reservationKey())
        tenantIdToReservationMap.computeIfAbsent(tenantId) { HashSet() }.add(reservation.reservationKey())
    }

    fun getReservationsForApartment(apartmentId: String): Collection<ReservationDto> =
        apartmentIdToReservationMap[apartmentId]?.mapNotNull { reservationKeyToReservationMap[it] }?.toSet() ?: emptySet()

    fun getReservationsForTenant(tenantId: String): Collection<ReservationDto> =
        tenantIdToReservationMap[tenantId]?.mapNotNull { reservationKeyToReservationMap[it] }?.toSet() ?: emptySet()

    fun getReservation(apartmentId: String, date: LocalDate, period: Int) =
        reservationKeyToReservationMap[ReservationKey(apartmentId, date, period)]

    fun changeStatus(reservation: ReservationDto, newStatus: ReservationStatus) {
        reservationKeyToReservationMap[reservation.reservationKey()] = reservation.copy(status = newStatus)
    }

    fun getAll() =
        reservationKeyToReservationMap.values
            .sortedBy { it.apartmentId }
}