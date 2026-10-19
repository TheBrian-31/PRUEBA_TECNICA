package com.thebrian31.coworking.service;

import java.time.OffsetDateTime;

public record ReservationConfirmedEvent(
        Long reservationId,
        String email,
        Long spaceId,
        OffsetDateTime startAt,
        OffsetDateTime endAt
) {
}
