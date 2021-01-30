package internal.core.database

import internal.core.logging.logger
import org.jvnet.hk2.annotations.Service

@Service
class Transactor {
    private companion object {
        private val logger = logger()
        private val sync = Any()
    }

    internal val transactionOpen = ThreadLocal.withInitial { false }

    fun <R> inTransaction(callback: () -> R): R =
        synchronized(sync) {
            createTransaction(readOnly = false, callback = callback)
        }

    fun <R> inReadOnlyTransaction(callback: () -> R): R =
        createTransaction(readOnly = true, callback = callback)

    private fun <R> createTransaction(readOnly: Boolean, callback: () -> R): R {
        if (transactionOpen.get()) {
            logger.debug("Using nested transaction.")
            return callback.invoke()
        }
        try {
            transactionOpen.set(true)
            logger.debug("Transaction begin [readOnly=$readOnly].")
            val result = callback.invoke()
            logger.debug("Transaction commit.")
            return result
        } catch (e: Throwable) {
            logger.error("Transaction rollback.", e)
            throw e
        } finally {
            transactionOpen.set(false)
        }
    }
}