package com.thebrian31.coworking.dto;

import java.math.BigDecimal;

public record SpaceOccupancyResponse(
        Long spaceId,
        String name,
        BigDecimal occupancyPercent
) {
}
