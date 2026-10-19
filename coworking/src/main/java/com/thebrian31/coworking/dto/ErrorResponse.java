package com.thebrian31.coworking.dto;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
