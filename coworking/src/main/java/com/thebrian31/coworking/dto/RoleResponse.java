package com.thebrian31.coworking.dto;

import com.thebrian31.coworking.entity.RoleCode;

public record RoleResponse(
        Short id,
        RoleCode code,
        String description
) {
}
