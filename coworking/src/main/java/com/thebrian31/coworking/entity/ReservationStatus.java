package com.thebrian31.coworking.entity;

public enum ReservationStatus {
    PENDING,
    PENDING_PAYMENT,
    CONFIRMED,
    CANCELLED,
    COMPLETED;

    public boolean canTransitionTo(ReservationStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == PENDING_PAYMENT || next == CANCELLED;
            case PENDING_PAYMENT -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == COMPLETED || next == CANCELLED;
            case CANCELLED, COMPLETED -> false;
        };
    }
}
