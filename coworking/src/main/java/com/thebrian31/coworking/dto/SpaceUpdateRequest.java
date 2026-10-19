package com.thebrian31.coworking.dto;

import com.thebrian31.coworking.entity.SpaceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SpaceUpdateRequest(
        @Size(max = 120)
        String name,

        SpaceType type,

        @Min(1)
        Integer capacity,

        @Size(max = 120)
        String location,

        @DecimalMin("0.00")
        BigDecimal hourlyRate,

        Boolean outOfService
) {
}
