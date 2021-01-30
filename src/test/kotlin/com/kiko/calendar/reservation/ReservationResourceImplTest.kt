package com.kiko.calendar.reservation

import com.kiko.calendar.Fixtures.reservation
import com.kiko.calendar.jacksonJsonProvider
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Before
import org.junit.Test
import javax.ws.rs.core.Application

class ReservationResourceImplTest : JerseyTest() {

    private lateinit var reservationService: ReservationService

    private lateinit var client: ReservationResource

    override fun configure(): Application {
        reservationService = mock()
        return ResourceConfig()
            .register(
                ReservationResourceImpl(
                    reservationService = reservationService
                )
            )
            .register(jacksonJsonProvider)
    }

    override fun configureClient(config: ClientConfig) {
        config.register(jacksonJsonProvider)
    }

    @Before
    fun before() {
        client = WebResourceFactory.newResource(ReservationResource::class.java, target())
    }

    @Test
    fun `get all reservations`() {
        whenever(reservationService.getAll())
            .thenReturn(listOf(reservation))

        assertThat(client.getAll().values)
            .hasSize(1)
            .containsExactly(reservation)
    }
}