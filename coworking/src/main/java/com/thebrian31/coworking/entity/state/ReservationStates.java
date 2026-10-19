package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.ReservationStatus;

public final class ReservationStates {

    private ReservationStates() {
    }

    public static ReservationState of(ReservationStatus status) {
        return switch (status) {
            case PENDING -> PendingReservationState.INSTANCE;
            case PENDING_PAYMENT -> PendingPaymentReservationState.INSTANCE;
            case CONFIRMED -> ConfirmedReservationState.INSTANCE;
            case CANCELLED -> CancelledReservationState.INSTANCE;
            case COMPLETED -> CompletedReservationState.INSTANCE;
        };
    }
}
