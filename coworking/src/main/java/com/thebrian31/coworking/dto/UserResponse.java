package com.thebrian31.coworking.dto;

import com.thebrian31.coworking.entity.RoleCode;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        RoleCode role,
        boolean enabled,
        OffsetDateTime createdAt
) {
}
