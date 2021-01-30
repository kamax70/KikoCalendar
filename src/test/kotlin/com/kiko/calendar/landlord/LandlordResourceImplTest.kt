package com.kiko.calendar.landlord

import com.kiko.calendar.jacksonJsonProvider
import com.nhaarman.mockitokotlin2.mock
import com.kiko.calendar.landlord.LandlordResource.*
import com.nhaarman.mockitokotlin2.verify
import org.assertj.core.api.Assertions
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.test.JerseyTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Application

class LandlordResourceImplTest : JerseyTest() {

    private lateinit var landlordService: LandlordService

    private lateinit var client: LandlordResource

    override fun configure(): Application {
        landlordService = mock()
        return ResourceConfig()
            .register(
                LandlordResourceImpl(
                    landlordService = landlordService
                )
            )
            .register(jacksonJsonProvider)
    }

    override fun configureClient(config: ClientConfig) {
        config.register(jacksonJsonProvider)
    }

    @Before
    fun before() {
        client = WebResourceFactory.newResource(LandlordResource::class.java, target())
    }

    @Test
    fun `approve reservation`() {
        client.approveReservation(
            ApproveReservationRequest(
                date = LocalDate.parse("2020-01-01"),
                period = 5,
                apartmentId = "apartmentId"
            )
        )
        verify(landlordService).approveReservation(
            date = LocalDate.parse("2020-01-01"),
            period = 5,
            apartmentId = "apartmentId"
        )
    }

    @Test
    fun `cancel reservation`() {
        client.cancelReservation(
            LandlordCancelReservationRequest(
                date = LocalDate.parse("2020-01-01"),
                period = 5,
                apartmentId = "apartmentId"
            )
        )
        verify(landlordService).cancelReservation(
            date = LocalDate.parse("2020-01-01"),
            period = 5,
            apartmentId = "apartmentId"
        )
    }

    @Test
    fun `validation constraints`() {
        Assertions.assertThatThrownBy {
            client.approveReservation(
                ApproveReservationRequest(
                    date = LocalDate.parse("2020-01-01"),
                    period = 5,
                    apartmentId = ""
                )
            )
        }.isInstanceOf(BadRequestException::class.java)
    }
}