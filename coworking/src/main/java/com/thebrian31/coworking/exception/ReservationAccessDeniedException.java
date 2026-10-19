package com.thebrian31.coworking.exception;

public class ReservationAccessDeniedException extends RuntimeException {

    public ReservationAccessDeniedException() {
        super("No puedes gestionar esta reserva");
    }
}
