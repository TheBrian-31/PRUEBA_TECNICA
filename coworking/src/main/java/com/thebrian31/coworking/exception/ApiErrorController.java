package com.thebrian31.coworking.exception;

import com.thebrian31.coworking.dto.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        int statusCode = statusCode(request);
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = message(request, status);
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message));
    }

    private String message(HttpServletRequest request, HttpStatus status) {
        Object error = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (error instanceof OverlappingReservationException overlap) {
            return overlap.getMessage();
        }
        return switch (status) {
            case UNAUTHORIZED -> "Se requiere autenticación";
            case FORBIDDEN -> "No tienes permiso para acceder a este recurso";
            case NOT_FOUND -> "No existe el recurso solicitado";
            default ->
                    "No se pudo completar la petición";
        };
    }

    private int statusCode(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status instanceof Integer code) {
            return code;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}
