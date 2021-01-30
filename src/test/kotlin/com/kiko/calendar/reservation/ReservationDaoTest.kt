package com.kiko.calendar.reservation

import com.kiko.calendar.Fixtures.apartmentId
import com.kiko.calendar.Fixtures.date
import com.kiko.calendar.Fixtures.period
import com.kiko.calendar.Fixtures.reservation
import com.kiko.calendar.Fixtures.tenantId
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ReservationDaoTest {

    private val dao = ReservationDao()

    @Test
    fun `test dao`() {
        assertNull(dao.getReservation(apartmentId, date, period))
        assertThat(dao.getReservationsForApartment(apartmentId)).isEmpty()
        assertThat(dao.getReservationsForTenant(tenantId)).isEmpty()
        assertThat(dao.getAll()).isEmpty()

        dao.createReservation(date, period, apartmentId, tenantId)

        assertEquals(reservation, dao.getReservation(apartmentId, date, period))
        assertThat(dao.getReservationsForApartment(apartmentId)).containsExactly(reservation)
        assertThat(dao.getReservationsForTenant(tenantId)).containsExactly(reservation)
        assertThat(dao.getAll()).containsExactly(reservation)

        dao.changeStatus(reservation, ReservationStatus.APPROVED)
        val updatedReservation = reservation.copy(status = ReservationStatus.APPROVED)

        assertEquals(updatedReservation, dao.getReservation(apartmentId, date, period))
        assertThat(dao.getReservationsForApartment(apartmentId)).containsExactly(updatedReservation)
        assertThat(dao.getReservationsForTenant(tenantId)).containsExactly(updatedReservation)
        assertThat(dao.getAll()).containsExactly(updatedReservation)
    }
}