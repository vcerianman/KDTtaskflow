package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.deserializer.FlexibleStringListDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserUpdateRequest(
        @JsonAlias({"full_name", "fullname"})
        @Size(max = 120, message = "Full name max 120 characters")
        String fullName,

        @Email(message = "Email format is invalid")
        @Size(max = 120, message = "Email max 120 characters")
        String email,

        UserStatus status,

        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password,

        @JsonDeserialize(using = FlexibleStringListDeserializer.class)
        List<String> about,

        @JsonDeserialize(using = FlexibleStringListDeserializer.class)
        List<String> projects
) {}
