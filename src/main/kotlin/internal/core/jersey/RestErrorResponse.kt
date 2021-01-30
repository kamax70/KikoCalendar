package internal.core.jersey

data class RestErrorResponse(
    val code: String,
    val message: String
)