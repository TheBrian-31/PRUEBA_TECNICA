package com.thebrian31.coworking.controller;

import com.thebrian31.coworking.dto.ReservationRequest;
import com.thebrian31.coworking.dto.ReservationResponse;
import com.thebrian31.coworking.entity.ReservationStatus;
import com.thebrian31.coworking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationRequest request) {
        return reservationService
                .create(request);
    }

    @GetMapping
    public List<ReservationResponse> findAll(@RequestParam(required = false) ReservationStatus status) {
        return reservationService.findAll(status);
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable  Long id) {
        return reservationService.findById(id);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable Long id) {
        return reservationService.cancel(id);
    }

    @PostMapping("/{id}/confirm")
    public ReservationResponse confirm(@PathVariable Long id) {
        return reservationService.confirm(id);
    }

    @PostMapping("/{id}/complete")
    public ReservationResponse complete(@PathVariable Long id) {
        return reservationService.complete(id);
    }
}
