package com.thebrian31.coworking.entity.state;

import com.thebrian31.coworking.entity.Reservation;
import com.thebrian31.coworking.entity.ReservationStatus;
import com.thebrian31.coworking.exception.InvalidReservationException;

public interface ReservationState {

    ReservationStatus status();

    default void confirm(Reservation reservation) {
        reject("confirmar");
    }

    default void cancel(Reservation reservation) {
        reject("cancelar");
    }

    default void complete(Reservation reservation) {
        reject("completar");
    }

    default void awaitPayment(Reservation reservation) {
        reject("dejar pendiente de pago");
    }

    private void reject(String action) {
        throw new InvalidReservationException("No se puede " + action + " una reserva " + status());
    }
}
