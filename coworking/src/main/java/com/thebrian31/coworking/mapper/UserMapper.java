package com.thebrian31.coworking.mapper;

import com.thebrian31.coworking.dto.UserResponse;
import com.thebrian31.coworking.entity.Role;
import com.thebrian31.coworking.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(String fullName, String email, Role role, String passwordHash) {
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim()
                .toLowerCase());
        user.setPasswordHash(passwordHash);

        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getCode(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
