package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.CheckPasswordRequest;
import com.kdt.taskflow.dto.LoginRequest;
import com.kdt.taskflow.dto.LoginResponse;
import com.kdt.taskflow.dto.UserResponse;

import com.kdt.taskflow.dto.SignupRequest;

public interface AuthService {
    LoginResponse signup(SignupRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse me(String username);

    void checkPassword(String username, CheckPasswordRequest request);
}
