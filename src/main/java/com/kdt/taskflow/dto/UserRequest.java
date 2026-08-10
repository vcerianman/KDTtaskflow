package com.kdt.taskflow.dto;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
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

        @Size(max = 120, message = "Full name max 120 characters")
        String fullName,

        UserRole role
) {
    public User toDomain() {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);
        u.setFullName(fullName);
        u.setRole(role != null ? role : UserRole.MEMBER);
        return u;
    }
}
