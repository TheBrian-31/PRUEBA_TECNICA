package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;

public final class ConfirmedReservationState implements ReservationState {

    public static final ConfirmedReservationState INSTANCE = new ConfirmedReservationState();

    private ConfirmedReservationState() {
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.CONFIRMED;
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }


    @Override
    public void complete(Reservation reservation) {
        reservation.setStatus(
                ReservationStatus.COMPLETED);
    }
}
