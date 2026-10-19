package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;

public final class PendingReservationState implements ReservationState {

    public static final PendingReservationState INSTANCE = new PendingReservationState();


    private PendingReservationState() {
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.PENDING;
    }

    @Override
    public void confirm(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public void awaitPayment(Reservation reservation) {
        reservation.setStatus(ReservationStatus.PENDING_PAYMENT);
    }
}
