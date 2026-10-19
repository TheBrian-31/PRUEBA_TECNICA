package com.thebrian31.coworking.mapper;

import com.thebrian31.coworking.dto.ReservationResponse;
import com.thebrian31.coworking.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getAccount().getId(),
                reservation.getSpace().getId(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getStatus(),

                reservation.getCreatedAt()
        );
    }
}
