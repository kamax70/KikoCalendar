package com.kiko.calendar.reservation

import com.kiko.calendar.Fixtures.apartmentId
import com.kiko.calendar.Fixtures.date
import com.kiko.calendar.Fixtures.landlordId
import com.kiko.calendar.Fixtures.period
import com.kiko.calendar.Fixtures.reservation
import com.kiko.calendar.Fixtures.tenantId
import com.kiko.calendar.landlord.LandlordDao
import com.kiko.calendar.landlord.LandlordDto
import com.kiko.calendar.reservation.event.ReservationCreatedEvent
import com.kiko.calendar.reservation.ReservationStatus.*
import com.kiko.calendar.reservation.event.ReservationApprovedEvent
import com.kiko.calendar.reservation.event.ReservationCanceledEvent
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import internal.core.database.Transactor
import internal.core.database.newTransactorMock
import internal.core.eventbus.EventManager
import internal.core.exception.BusinessValidationException
import junit.framework.Assert.assertEquals
import org.assertj.core.api.Assertions.*
import org.junit.Test

class ReservationServiceTest {

    private val landlordDao: LandlordDao = mock()
    private val reservationDao: ReservationDao = mock()
    private val transactor: Transactor = newTransactorMock()
    private val eventManager: EventManager = mock()
    private val service = ReservationService(landlordDao, reservationDao, transactor, eventManager)

    @Test
    fun `create reservation`() {
        whenever(landlordDao.findLandlordByApartmentId(apartmentId))
            .thenReturn(LandlordDto(landlordId))

        service.createReservation(date, period, apartmentId, tenantId)

        verify(reservationDao).createReservation(date, period, apartmentId, tenantId)
        verify(eventManager).notify(
            ReservationCreatedEvent(date, period, apartmentId, landlordId)
        )
    }

    @Test
    fun `can't find landlord`() {
        whenever(landlordDao.findLandlordByApartmentId(apartmentId))
            .thenReturn(null)

        assertThatThrownBy {
            service.createReservation(date, period, apartmentId, tenantId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Apartment with id 'apartmentId' not found.")

        assertThatThrownBy {
            service.cancelReservationByLandlord(reservation)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Apartment with id 'apartmentId' not found.")

        assertThatThrownBy {
            service.cancelReservationByTenant(reservation)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Apartment with id 'apartmentId' not found.")

    }

    @Test
    fun `approve reservation`() {
        service.approveReservation(reservation)

        verify(reservationDao).changeStatus(reservation, APPROVED)
        verify(eventManager).notify(
            ReservationApprovedEvent(date, period, apartmentId, tenantId)
        )
    }

    @Test
    fun `cancel reservation by landlord`() {
        whenever(landlordDao.findLandlordByApartmentId(apartmentId))
            .thenReturn(LandlordDto(landlordId))

        service.cancelReservationByLandlord(reservation)

        verify(reservationDao).changeStatus(reservation, CANCELED_BY_LANDLORD)
        verify(eventManager).notify(
            ReservationCanceledEvent(date, period, apartmentId, landlordId, tenantId, CANCELED_BY_LANDLORD)
        )
    }

    @Test
    fun `cancel reservation by tenant`() {
        whenever(landlordDao.findLandlordByApartmentId(apartmentId))
            .thenReturn(LandlordDto(landlordId))

        service.cancelReservationByLandlord(reservation)

        verify(reservationDao).changeStatus(reservation, CANCELED_BY_LANDLORD)
        verify(eventManager).notify(
            ReservationCanceledEvent(date, period, apartmentId, landlordId, tenantId, CANCELED_BY_LANDLORD)
        )
    }

    @Test
    fun `get reservation`() {
        whenever(reservationDao.getReservation(apartmentId, date, period))
            .thenReturn(reservation)

        assertEquals(reservation, service.getReservation(apartmentId, date, period))
    }

    @Test
    fun `get reservations for apartment`() {
        whenever(reservationDao.getReservationsForApartment(apartmentId))
            .thenReturn(listOf(reservation))

        assertThat(service.getReservationsForApartment(apartmentId))
            .containsExactly(reservation)
    }

    @Test
    fun `get reservations for tenant`() {
        whenever(reservationDao.getReservationsForTenant(tenantId))
            .thenReturn(listOf(reservation))

        assertThat(service.getReservationsForTenant(tenantId))
            .containsExactly(reservation)
    }

    @Test
    fun `get all`() {
        whenever(reservationDao.getAll())
            .thenReturn(listOf(reservation))

        assertThat(service.getAll())
            .containsExactly(reservation)
    }
}