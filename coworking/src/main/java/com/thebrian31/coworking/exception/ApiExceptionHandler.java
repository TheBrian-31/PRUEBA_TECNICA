package com.thebrian31.coworking.exception;

import com.thebrian31.coworking.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .findFirst()
                .orElse("La petición no es válida");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    ResponseEntity<ErrorResponse> handleDuplicateEmail(EmailAlreadyRegisteredException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(SpaceNotFoundException.class)
    ResponseEntity<ErrorResponse> handleSpaceNotFound(SpaceNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidSpaceUpdateException.class)
    ResponseEntity<ErrorResponse> handleInvalidSpaceUpdate(InvalidSpaceUpdateException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(SpaceInUseException.class)
    ResponseEntity<ErrorResponse> handleSpaceInUse(SpaceInUseException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    ResponseEntity<ErrorResponse> handleReservationNotFound(ReservationNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(OverlappingReservationException.class)
    ResponseEntity<ErrorResponse> handleOverlap(OverlappingReservationException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ReservationAccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleReservationAccessDenied(ReservationAccessDeniedException exception) {
        return error(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(InvalidReservationException.class)
    ResponseEntity<ErrorResponse> handleInvalidReservation(InvalidReservationException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(InvalidReportRangeException.class)
    ResponseEntity<ErrorResponse> handleInvalidReportRange(InvalidReportRangeException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> handleBadCredentials() {
        return error(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
    }

    @ExceptionHandler(DisabledException.class)

    ResponseEntity<ErrorResponse> handleDisabled() {
        return error(HttpStatus.UNAUTHORIZED, "La cuenta está deshabilitada");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message));
    }
}
