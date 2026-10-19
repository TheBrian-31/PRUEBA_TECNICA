package com.thebrian31.coworking.exception;

public class OverlappingReservationException extends RuntimeException {

    public OverlappingReservationException() {
        super("Ya existe una reserva para ese espacio en el horario indicado");
    }
}
