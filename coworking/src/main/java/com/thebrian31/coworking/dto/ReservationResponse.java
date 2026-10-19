package com.thebrian31.coworking.dto;

import com.thebrian31.coworking.entity.ReservationStatus;

import java.time.OffsetDateTime;

public record ReservationResponse(
        Long id,
        Long accountId,
        Long spaceId,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        ReservationStatus status,
        OffsetDateTime createdAt
) {
}
