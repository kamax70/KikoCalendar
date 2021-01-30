package internal.core.jersey;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface Resource {

    data class WrappedList<T>(
        val values: List<T>
    )
}


