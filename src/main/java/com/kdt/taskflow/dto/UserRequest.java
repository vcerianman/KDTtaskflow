package com.kdt.taskflow.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.deserializer.FlexibleStringListDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserRequest(
        @NotBlank(message = "Username cannot be blank!") @Size(min = 2, max = 80, message = "Username must be between 2 and 80 characters") String username,

        @NotBlank(message = "Email cannot be blank!") @Email(message = "Email format is invalid") @Size(max = 120, message = "Email max 120 characters") String email,

        @NotBlank(message = "Password cannot be blank!") @Size(min = 6, max = 100, message = "Password must be at least 6 characters") String password,

        @Size(max = 120, message = "Full name max 120 characters") String fullName,

        @JsonDeserialize(using = FlexibleStringListDeserializer.class) List<String> about,

        @JsonDeserialize(using = FlexibleStringListDeserializer.class) List<String> projects,

        UserRole role,

        UserStatus status,

        @Pattern(regexp = "^$|^https://res\\.cloudinary\\.com/bs5wxybt/.+$", message = "Avatar URL must be a valid Cloudinary URL") String avatarUrl,

        @Size(max = 255, message = "Avatar public ID max 255 characters") String avatarPublicId,

        @Pattern(regexp = "^$|^https://res\\.cloudinary\\.com/bs5wxybt/.+$", message = "Intro video URL must be a valid Cloudinary URL") String introVideoUrl,

        @Size(max = 255, message = "Intro video public ID max 255 characters") String introVideoPublicId) {
    public User toDomain() {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);
        u.setFullName(fullName);
        u.setAbout(about);
        u.setProjects(projects);
        u.setRole(role);
        u.setStatus(status != null ? status : UserStatus.ACTIVE);
        u.setAvatarUrl(avatarUrl != null && !avatarUrl.isBlank() ? avatarUrl : null);
        u.setAvatarPublicId(avatarPublicId != null && !avatarPublicId.isBlank() ? avatarPublicId : null);
        u.setIntroVideoUrl(introVideoUrl != null && !introVideoUrl.isBlank() ? introVideoUrl : null);
        u.setIntroVideoPublicId(
                introVideoPublicId != null && !introVideoPublicId.isBlank() ? introVideoPublicId : null);
        return u;
    }
}