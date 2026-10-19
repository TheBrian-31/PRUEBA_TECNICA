package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.ReservationStatus;

public final class CancelledReservationState implements ReservationState {

    public static final CancelledReservationState INSTANCE = new CancelledReservationState();

    private CancelledReservationState() {
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.CANCELLED;
    }
}
