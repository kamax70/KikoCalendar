package com.kiko.calendar.notification

import com.kiko.calendar.reservation.ReservationStatus.*
import com.kiko.calendar.reservation.event.ReservationApprovedEvent
import com.kiko.calendar.reservation.event.ReservationCanceledEvent
import com.kiko.calendar.reservation.event.ReservationCreatedEvent
import internal.core.eventbus.EventHandler
import internal.core.exception.Exceptions.internal
import org.jvnet.hk2.annotations.Service
import javax.inject.Inject

@Service
class ReservationCreatedEventHandler @Inject constructor(
    private val notificationService: NotificationService
) : EventHandler<ReservationCreatedEvent> {

    override fun handle(event: ReservationCreatedEvent) {
        notificationService.sendNotification(
            forUser = event.landlordId,
            message = "Reservation for apartment '${event.apartmentId}' has been created."
        )
    }

    override fun getEventClass() = ReservationCreatedEvent::class
}

@Service
class ReservationApprovedEventHandler @Inject constructor(
    private val notificationService: NotificationService
) : EventHandler<ReservationApprovedEvent> {

    override fun handle(event: ReservationApprovedEvent) {
        notificationService.sendNotification(
            forUser = event.tenantId,
            message = "Reservation for apartment '${event.apartmentId}' has been approved."
        )
    }

    override fun getEventClass() = ReservationApprovedEvent::class
}

@Service
class ReservationCanceledEventHandler @Inject constructor(
    private val notificationService: NotificationService
) : EventHandler<ReservationCanceledEvent> {

    override fun handle(event: ReservationCanceledEvent) {
        val forUser = when(event.status) {
            CANCELED_BY_LANDLORD -> event.landlordId
            CANCELED_BY_TENANT -> event.tenantId
            NEW, APPROVED -> throw internal("Invalid reservation status '${event.status}' during cancelation.")
        }
        notificationService.sendNotification(
            forUser = forUser,
            message = "Reservation for apartment '${event.apartmentId}' has been canceled."
        )
    }

    override fun getEventClass() = ReservationCanceledEvent::class
}