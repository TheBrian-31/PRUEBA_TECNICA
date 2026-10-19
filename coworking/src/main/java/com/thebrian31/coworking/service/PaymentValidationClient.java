package com.thebrian31.coworking.service;

import com.thebrian31.coworking.config.PaymentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaymentValidationClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentValidationClient.class);

    private final RestClient paymentRestClient;
    private final PaymentProperties properties;
    private final CircuitBreaker paymentCircuitBreaker;

    public PaymentValidationClient(
            RestClient paymentRestClient,
            PaymentProperties properties,
            CircuitBreaker paymentCircuitBreaker
    ) {
        this.paymentRestClient = paymentRestClient;
        this.properties = properties;
        this.paymentCircuitBreaker = paymentCircuitBreaker;
    }

    public PaymentOutcome validate(Long reservationId) {
        return paymentCircuitBreaker.run(() -> callPaymentService(reservationId), this::fallback);
    }

    private PaymentOutcome callPaymentService(Long reservationId) {
        PaymentValidationResponse response = paymentRestClient.post()
                .uri(properties.url())
                .body(new PaymentValidationRequest(reservationId))
                .retrieve()
                .body(PaymentValidationResponse.class);
        if (isApprooved(response)) {
            return PaymentOutcome.APPROVED;
        }
        return PaymentOutcome.REJECTED;
    }

    private boolean isApprooved(PaymentValidationResponse response) {
        return response != null && response.approved();
    }

    private PaymentOutcome fallback(Throwable throwable) {
        log.warn("La validación de pago no está disponible: {}", throwable.getMessage());
        return PaymentOutcome.UNAVAILABLE;
    }

    private record PaymentValidationRequest(Long reservationId) {
    }

    private record PaymentValidationResponse(boolean approved) {
    }
}
