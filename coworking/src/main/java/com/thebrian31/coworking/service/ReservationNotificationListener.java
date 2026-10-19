package com.thebrian31.coworking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReservationNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationNotificationListener.class);


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationConfirmed(ReservationConfirmedEvent event) {
        log.info(
                "Correo simulado enviado a {} por la reserva {} del espacio {} entre {} y {}",
                event.email(),
                event.reservationId(),
                event.spaceId(),
                event.startAt(),
                event.endAt()
        );
    }
}
