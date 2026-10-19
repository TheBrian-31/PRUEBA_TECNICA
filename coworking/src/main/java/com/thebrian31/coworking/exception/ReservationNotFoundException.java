package com.thebrian31.coworking.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("No existe la reserva " + id);
    }
}
