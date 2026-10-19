package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;

public final class PendingPaymentReservationState implements ReservationState {

    public static final PendingPaymentReservationState INSTANCE = new PendingPaymentReservationState();

    private PendingPaymentReservationState() {
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.PENDING_PAYMENT;
    }

    @Override
    public void confirm(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }
}
