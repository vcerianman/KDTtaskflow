package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
                @NotBlank(message = "Username cannot be blank") String username,

                @NotBlank(message = "Email cannot be blank") @Email(message = "Invalid email format") String email,

                String code,

                @JsonProperty("new_password") @JsonAlias({ "newPassword", "new_password" }) String newPassword) {
}
