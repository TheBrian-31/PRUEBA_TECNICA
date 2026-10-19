package com.thebrian31.coworking.mapper;

import com.thebrian31.coworking.dto.RoleResponse;
import com.thebrian31.coworking.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return new RoleResponse(role.getId(),
                role.getCode(), role.getDescription());
    }
}
