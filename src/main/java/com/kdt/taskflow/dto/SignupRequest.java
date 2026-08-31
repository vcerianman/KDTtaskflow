package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "Username cannot be blank!")
        @Size(min = 2, max = 80, message = "Username must be between 2 and 80 characters")
        String username,

        @NotBlank(message = "Email cannot be blank!")
        @Email(message = "Email format is invalid")
        @Size(max = 120, message = "Email max 120 characters")
        String email,

        @NotBlank(message = "Password cannot be blank!")
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password,

        @JsonAlias({"fullname", "full_name"})
        @Size(max = 120, message = "Full name max 120 characters")
        String fullName
) {
}
