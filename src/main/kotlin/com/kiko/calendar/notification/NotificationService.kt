package com.kiko.calendar.notification

import internal.core.logging.logger
import org.jvnet.hk2.annotations.Service

@Service
class NotificationService {

    private companion object {
        val logger = logger()
    }

    fun sendNotification(forUser: String, message: String) {
        logger.info("Notification '$message' for user '$forUser' has been sent.")
    }
}