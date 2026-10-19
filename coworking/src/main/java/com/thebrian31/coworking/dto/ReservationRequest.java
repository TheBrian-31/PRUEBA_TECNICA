package com.thebrian31.coworking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ReservationRequest(
        @NotNull
        Long spaceId,

        @NotNull
        OffsetDateTime startAt,

        @NotNull
        OffsetDateTime endAt

) {
}
