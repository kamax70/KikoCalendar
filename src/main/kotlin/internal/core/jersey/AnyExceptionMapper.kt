package internal.core.jersey

import internal.core.exception.BusinessValidationException
import internal.core.logging.logger
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class AnyExceptionMapper : ExceptionMapper<Throwable> {

    private companion object {
        val logger = logger()
    }

    override fun toResponse(originalException: Throwable): Response {
        val (response, status) = if (originalException is BusinessValidationException) {
            RestErrorResponse(originalException.code, originalException.message) to 400
        } else {
            logger.error("Unhandled exception", originalException)
            RestErrorResponse("UNHANDLED_EXCEPTION", originalException.message.orEmpty()) to 500
        }
        return Response.status(status).entity(response).type(MediaType.APPLICATION_JSON).build()
    }
}

