package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.domain.UserRole;
import com.kdt.taskflow.domain.UserStatus;
import com.kdt.taskflow.dto.CheckPasswordRequest;
import com.kdt.taskflow.dto.LoginRequest;
import com.kdt.taskflow.dto.LoginResponse;
import com.kdt.taskflow.dto.SignupRequest;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.exception.ResourceNotFoundException;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.security.JwtUtils;
import com.kdt.taskflow.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public LoginResponse signup(SignupRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();

        if (userMapper.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userMapper.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName() != null && !request.fullName().isBlank() ? request.fullName().trim() : username);
        user.setRole(UserRole.MEMBER);
        user.setStatus(UserStatus.ACTIVE);
        user.setAbout(List.of());
        user.setProjects(List.of());

        userMapper.insert(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return LoginResponse.of(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    // 1. Fetch user by username
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        // 2. Compare raw input password with the stored BCrypt hash
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return LoginResponse.of(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(String username) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkPassword(String username, CheckPasswordRequest request) {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
}
