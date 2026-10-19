package com.thebrian31.coworking.exception;

public class SpaceNotFoundException extends RuntimeException {

    public SpaceNotFoundException(Long id) {

        super("No existe el espacio " + id);
    }
}
