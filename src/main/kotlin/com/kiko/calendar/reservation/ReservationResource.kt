package com.kiko.calendar.reservation

import internal.core.jersey.Resource
import internal.core.jersey.Resource.WrappedList
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api("Reservation API")
@Path("/reservation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface ReservationResource : Resource {

    @GET
    @ApiOperation("Get all reservations.")
    fun getAll(): WrappedList<ReservationDto>
}

class ReservationResourceImpl @Inject constructor(
    private val reservationService: ReservationService
) : ReservationResource {
    override fun getAll(): WrappedList<ReservationDto> =
        WrappedList(reservationService.getAll())
}