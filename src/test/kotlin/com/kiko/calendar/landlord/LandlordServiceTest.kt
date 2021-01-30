package com.kiko.calendar.landlord

import com.kiko.calendar.Fixtures.apartmentId
import com.kiko.calendar.Fixtures.date
import com.kiko.calendar.Fixtures.period
import com.kiko.calendar.Fixtures.reservation
import com.kiko.calendar.reservation.ReservationService
import com.kiko.calendar.reservation.ReservationStatus.*
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import internal.core.database.Transactor
import internal.core.database.newTransactorMock
import internal.core.exception.BusinessValidationException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.time.LocalDate

class LandlordServiceTest {

    private val reservationService: ReservationService = mock()
    private val transactor: Transactor = newTransactorMock()
    private val service = LandlordService(reservationService, transactor)

    @Test
    fun `approve reservation`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation)

        service.approveReservation(date, period, apartmentId)

        verify(reservationService).approveReservation(reservation)
    }

    @Test
    fun `approve reservation - reservation not found`() {
        whenever(reservationService.getReservation(apartmentId, date, period)).thenReturn(null)

        assertThatThrownBy {
            service.approveReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation for date=[2020-01-01], period=[5], apartmentId=[apartmentId] doesn't exist.")
    }

    @Test
    fun `approve reservation - reservation canceled`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = CANCELED_BY_TENANT))
        assertThatThrownBy {
            service.approveReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation is canceled.")

        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = CANCELED_BY_LANDLORD))
        assertThatThrownBy {
            service.approveReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation is canceled.")
    }

    @Test
    fun `approve reservation - reservation approved`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = APPROVED))
        assertThatThrownBy {
            service.approveReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation is already approved.")
    }

    @Test
    fun `cancel reservation`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation)
        service.cancelReservation(date, period, apartmentId)
        verify(reservationService).cancelReservationByLandlord(reservation)

        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = APPROVED))
        service.cancelReservation(date, period, apartmentId)
        verify(reservationService).cancelReservationByLandlord(reservation)
    }

    @Test
    fun `cancel reservation - reservation already canceled`() {
        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = CANCELED_BY_LANDLORD))
        assertThatThrownBy {
            service.cancelReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation is already canceled.")

        whenever(reservationService.getReservation(apartmentId, date, period))
            .thenReturn(reservation.copy(status = CANCELED_BY_TENANT))
        assertThatThrownBy {
            service.cancelReservation(date, period, apartmentId)
        }.isInstanceOf(BusinessValidationException::class.java)
            .hasMessage("Reservation is already canceled.")
    }
}