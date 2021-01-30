package com.kiko.calendar.tenant

import com.kiko.calendar.jacksonJsonProvider
import com.kiko.calendar.tenant.TenantResource.*
import com.nhaarman.mockitokotlin2.mock
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

class TenantResourceImplTest : JerseyTest() {

    private lateinit var tenantService: TenantService

    private lateinit var client: TenantResource

    override fun configure(): Application {
        tenantService = mock()
        return ResourceConfig()
            .register(
                TenantResourceImpl(
                    tenantService = tenantService
                )
            )
            .register(jacksonJsonProvider)
    }

    override fun configureClient(config: ClientConfig) {
        config.register(jacksonJsonProvider)
    }

    @Before
    fun before() {
        client = WebResourceFactory.newResource(TenantResource::class.java, target())
    }

    @Test
    fun `create reservation`() {
        client.createReservation(
            CreateReservationRequest(
                date = LocalDate.parse("2020-01-01"),
                period = 5,
                apartmentId = "apartmentId",
                tenantId = "tenantId"
            )
        )
        verify(tenantService).createReservation(
            date = LocalDate.parse("2020-01-01"),
            period = 5,
            apartmentId = "apartmentId",
            tenantId = "tenantId"
        )
    }

    @Test
    fun `cancel reservation`() {
        client.cancelReservation(
            CancelReservationRequest(
                date = LocalDate.parse("2020-01-01"),
                period = 5,
                apartmentId = "apartmentId",
                tenantId = "tenantId"
            )
        )
        verify(tenantService).cancelReservation(
            date = LocalDate.parse("2020-01-01"),
            period = 5,
            apartmentId = "apartmentId",
            tenantId = "tenantId"
        )
    }

    @Test
    fun `validation constraints`() {
        Assertions.assertThatThrownBy {
            client.createReservation(
                CreateReservationRequest(
                    date = LocalDate.parse("2020-01-01"),
                    period = 5,
                    apartmentId = "",
                    tenantId = ""
                )
            )
        }.isInstanceOf(BadRequestException::class.java)
    }
}