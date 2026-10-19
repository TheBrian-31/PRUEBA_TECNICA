package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.ReservationStatus;

public final class CompletedReservationState implements ReservationState {

    public static final CompletedReservationState INSTANCE = new CompletedReservationState();

    private CompletedReservationState() {
    }

    @Override
    public ReservationStatus status() {
        return ReservationStatus.COMPLETED;
    }
}
