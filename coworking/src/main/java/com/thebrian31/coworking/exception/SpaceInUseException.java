package com.thebrian31.coworking.exception;

public class SpaceInUseException extends RuntimeException {

    public SpaceInUseException(Long id) {
        super("El espacio " + id + " tiene reservas y no se puede eliminar");
    }
}
