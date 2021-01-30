package com.kiko.calendar.tenant

import com.kiko.calendar.Fixtures.apartmentId
import com.kiko.calendar.Fixtures.date
import com.kiko.calendar.Fixtures.period
import com.kiko.calendar.Fixtures.reservation
import com.kiko.calendar.Fixtures.tenantId
import com.kiko.calendar.reservation.DateRangeValidator
import com.kiko.calendar.reservation.ReservationService
import com.kiko.calendar.reservation.ReservationStatus
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import internal.core.database.Transactor
import internal.core.database.newTransactorMock
import internal.core.exception.BusinessValidationException
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test

class TenantServiceTest {
    private val reservationService: ReservationService = mock()
    private val dateRangeValidator: DateRangeValidator = mock()
    private val transactor: Transactor = newTransactorMock()
    private val service = TenantService(reservationService, dateRangeValidator, transactor)

    @Before
    fun before() {
        whenever(dateRangeValidator.isValid(date, period))
            .thenReturn(true)
    }

    @Test
    fun `invalid period`() {
        whenever(dateRangeValidator.isValid(date, period))
            .thenReturn(false)

        assertThatThrownBy {
            service.createReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation for date=[2020-01-01], period=[5] is forbidden.")
    }

    @Test
    fun `create reservation`() {
        whenever(reservationService.getReservationsForApartment(apartmentId))
            .thenReturn(emptyList())
        whenever(reservationService.getReservationsForTenant(tenantId))
            .thenReturn(emptyList())

        service.createReservation(tenantId, date, period, apartmentId)

        verify(reservationService).createReservation(date, period, apartmentId, tenantId)
    }

    @Test
    fun `cancel reservation`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation)

        service.cancelReservation(tenantId, date, period, apartmentId)

        verify(reservationService).cancelReservationByTenant(reservation)
    }

    @Test
    fun `cancel reservation - can't find reservation`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(null)

        assertThatThrownBy {
            service.cancelReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation for date=[2020-01-01], period=[5] doesn't exist.")
    }

    @Test
    fun `validate create reservation`() {
        whenever(reservationService.getReservationsForApartment(apartmentId))
            .thenReturn(listOf(reservation))
        assertThatThrownBy {
            service.createReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Tenant has already reserved that apartment.")
        // -----
        whenever(reservationService.getReservationsForApartment(apartmentId))
            .thenReturn(listOf(reservation.copy(tenantId = "anotherTenant")))
        assertThatThrownBy {
            service.createReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Apartment is already reserved for that time by another tenant.")
        // -----
        whenever(reservationService.getReservationsForApartment(apartmentId))
            .thenReturn(listOf(reservation.copy(status = ReservationStatus.CANCELED_BY_LANDLORD)))
        assertThatThrownBy {
            service.createReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Apartment can't be reserved at that time because landlord is busy.")
        // -----
        whenever(reservationService.getReservationsForApartment(apartmentId))
            .thenReturn(emptyList())
        whenever(reservationService.getReservationsForTenant(tenantId))
            .thenReturn(listOf(reservation.copy(apartmentId = "anotherApartment")))
        assertThatThrownBy {
            service.createReservation(tenantId, date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Tenant has another reserved apartment at that time.")
    }
}