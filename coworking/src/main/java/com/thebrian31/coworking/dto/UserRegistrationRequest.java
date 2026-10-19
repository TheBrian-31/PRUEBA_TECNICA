package com.thebrian31.coworking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank
        @Size(max = 120)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
