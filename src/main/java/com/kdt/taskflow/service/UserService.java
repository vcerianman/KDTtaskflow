package com.kdt.taskflow.service;

import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.ResetPasswordRequest;
import com.kdt.taskflow.dto.UserRequest;
import com.kdt.taskflow.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> search(UserRole role, String keyword);

    List<UserResponse> searchAdmin(String username, String email, UserRole role, UserStatus status);

    UserResponse update(Long id, UserRequest request);

    void resetPassword(Long id, ResetPasswordRequest request);

    void softDelete(Long id);

    void delete(Long id);
}
