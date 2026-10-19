package com.thebrian31.coworking.controller;

import com.thebrian31.coworking.config.PaymentProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/payments")
public class PaymentValidationController {

    private final PaymentProperties properties;

    public PaymentValidationController(PaymentProperties properties) {
        this.properties = properties;
    }

    @PostMapping("/validate")
    public PaymentValidationResponse validate(@RequestBody PaymentValidationRequest request) throws InterruptedException {
        if(!properties.delay().isZero()) {
            Thread.sleep(properties.delay().toMillis());
        }
        if (properties.forceFailure()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de pago no responde");
        }
        return new PaymentValidationResponse(request.reservationId(), true);
    }

    public record PaymentValidationRequest(Long reservationId) {
    }

    public record PaymentValidationResponse(Long reservationId, boolean approved) {
    }
}
