package com.fintrack.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDTO {

    public record UpdateRequest(
            @NotBlank(message = "First name is required")
            String firstName,

            @NotBlank(message = "Last name is required")
            String lastName,

            @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
            String phone,

            @Size(max = 255, message = "Address too long")
            String address
    ) {}

    public record UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            boolean active
    ) {}
}