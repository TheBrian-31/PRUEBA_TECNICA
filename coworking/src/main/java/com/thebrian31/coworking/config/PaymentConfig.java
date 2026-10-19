package com.thebrian31.coworking.config;

import com.thebrian31.coworking.service.PaymentOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    @Bean
    CircuitBreaker paymentCircuitBreaker(CircuitBreakerFactory<?, ?> factory) {
        return factory.create("payment");
    }

    @Bean
    RestClient paymentRestClient(PaymentProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.timeout());

        requestFactory.setReadTimeout(properties.timeout());
        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
