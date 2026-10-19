package com.thebrian31.coworking.dto;

import com.thebrian31.coworking.entity.SpaceType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SpaceResponse(
        Long id,
        String name,
        SpaceType type,
        Integer capacity,
        String location,
        BigDecimal hourlyRate,
        boolean outOfService,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
