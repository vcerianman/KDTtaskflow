package com.kdt.taskflow.service;

import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.ResetPasswordRequest;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> search(Long projectId, UserRole role, String keyword);

    List<UserResponse> searchAdmin(String username, String email, UserRole role, UserStatus status);

    UserResponse update(Long id, String currentUsername, UserUpdateRequest request);

    UserResponse updateAdmin(Long id, UserRequest request);

    void resetPassword(Long id, ResetPasswordRequest request);

    void softDelete(Long id);

    void delete(Long id);
}
