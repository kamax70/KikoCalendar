package internal.core.jersey

import internal.core.logging.logger
import java.util.Comparator
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class ConstraintViolationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
    override fun toResponse(exception: ConstraintViolationException): Response {
        val violation = exception
            .constraintViolations
            .asSequence()
            .sortedWith(Comparator.comparing { v: ConstraintViolation<*> ->
                v.propertyPath.toString()
            })
            .firstOrNull()
        return violation?.let { v: ConstraintViolation<*> ->
            val message = "${v.propertyPath.last()} ${v.message}"
            Response.status(Response.Status.BAD_REQUEST)
                .entity(RestErrorResponse("INVALID_ARGUMENT", message))
                .type(MediaType.APPLICATION_JSON)
                .build()
        } ?: run {
            logger.error("No constraint violations found though expected", exception)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(RestErrorResponse("UNHANDLED_EXCEPTION", "Internal Server Error"))
                .type(MediaType.APPLICATION_JSON)
                .build()
        }
    }

    private companion object {
        val logger = logger()
    }
}