package com.kiko.calendar.tenant

import com.kiko.calendar.reservation.DateRangeValidator.Companion.MAX_20_MIN_PERIODS_COUNT_IN_DAY
import internal.core.jersey.Resource
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import java.time.LocalDate
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api("Tenant API")
@Path("/tenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface TenantResource : Resource {

    @POST
    @Path("/reservation/create")
    @ApiOperation("Create reservation.")
    fun createReservation(
        @Valid request: CreateReservationRequest
    )

    @POST
    @Path("/reservation/cancel")
    @ApiOperation("Cancel reservation by tenant.")
    fun cancelReservation(
        @Valid request: CancelReservationRequest
    )

    data class CreateReservationRequest(
        @get:NotNull val date: LocalDate?,
        @get:NotNull @get:Min(0) @get:Max(MAX_20_MIN_PERIODS_COUNT_IN_DAY - 1) val period: Int?,
        @get:NotBlank val apartmentId: String?,
        @get:NotBlank val tenantId: String?
    )

    data class CancelReservationRequest(
        @get:NotNull val date: LocalDate?,
        @get:NotNull @get:Min(0) @get:Max(MAX_20_MIN_PERIODS_COUNT_IN_DAY - 1) val period: Int?,
        @get:NotBlank val apartmentId: String?,
        @get:NotBlank val tenantId: String?
    )
}

class TenantResourceImpl @Inject constructor(
    private val tenantService: TenantService
) : TenantResource {
    override fun createReservation(request: TenantResource.CreateReservationRequest) =
        with(request) {
            tenantService.createReservation(
                date = date!!,
                period = period!!,
                tenantId = tenantId!!,
                apartmentId = apartmentId!!
            )
        }

    override fun cancelReservation(request: TenantResource.CancelReservationRequest) =
        with(request) {
            tenantService.cancelReservation(
                apartmentId = apartmentId!!,
                tenantId = tenantId!!,
                period = period!!,
                date = date!!
            )
        }
}