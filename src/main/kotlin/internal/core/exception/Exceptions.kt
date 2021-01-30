package internal.core.exception

import java.lang.RuntimeException

class BusinessValidationException constructor(val code: String, override val message: String) : RuntimeException(message)

object Exceptions {

    @JvmStatic
    fun invalidArgument(message: String) = BusinessValidationException("INVALID_ARGUMENT", message)

    @JvmStatic
    fun validationException(message: String) = BusinessValidationException("VALIDATION_EXCEPTION", message)

    @JvmStatic
    fun notFound(message: String) = BusinessValidationException("NOT_FOUND", message)

    @JvmStatic
    fun internal(message: String) = BusinessValidationException("INTERNAL", message)
}