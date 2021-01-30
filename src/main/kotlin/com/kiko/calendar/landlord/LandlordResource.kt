package com.kiko.calendar.landlord

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

@Api("Landlord API")
@Path("/landlord")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface LandlordResource : Resource {

    @POST
    @Path("/reservation/approve")
    @ApiOperation("Approve reservation by landlord.")
    fun approveReservation(
        @Valid request: ApproveReservationRequest
    )

    @POST
    @Path("/reservation/cancel")
    @ApiOperation("Cancel reservation by landlord.")
    fun cancelReservation(
        @Valid request: LandlordCancelReservationRequest
    )

    data class ApproveReservationRequest(
        @get:NotNull val date: LocalDate?,
        @get:NotNull @get:Min(0) @get:Max(MAX_20_MIN_PERIODS_COUNT_IN_DAY - 1) val period: Int?,
        @get:NotBlank val apartmentId: String?
    )

    data class LandlordCancelReservationRequest(
        @get:NotNull val date: LocalDate?,
        @get:NotNull @get:Min(0) @get:Max(MAX_20_MIN_PERIODS_COUNT_IN_DAY - 1) val period: Int?,
        @get:NotBlank val apartmentId: String?
    )
}

class LandlordResourceImpl @Inject constructor(
    private val landlordService: LandlordService
) : LandlordResource {
    override fun approveReservation(request: LandlordResource.ApproveReservationRequest) =
        with(request) {
            landlordService.approveReservation(
                date = date!!,
                period = period!!,
                apartmentId = apartmentId!!
            )
        }

    override fun cancelReservation(request: LandlordResource.LandlordCancelReservationRequest) =
        with(request) {
            landlordService.cancelReservation(
                date = date!!,
                period = period!!,
                apartmentId = apartmentId!!
            )
        }
}