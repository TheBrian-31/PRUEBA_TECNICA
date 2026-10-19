package com.thebrian31.coworking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "payment.validation")
public record PaymentProperties(
        String url,
        Duration timeout,
        Duration delay,
        boolean forceFailure
) {
}
