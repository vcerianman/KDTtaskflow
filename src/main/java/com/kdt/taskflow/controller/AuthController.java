package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.CheckPasswordRequest;
import com.kdt.taskflow.dto.LoginRequest;
import com.kdt.taskflow.dto.LoginResponse;
import com.kdt.taskflow.dto.UserResponse;
import com.kdt.taskflow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login — Public endpoint.
     * Receives username + password, returns a JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * GET /api/auth/me — Protected endpoint.
     * Returns info of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(authService.me(username));
    }

    /**
     * POST /api/auth/password — Protected endpoint.
     * Checks if the provided password matches the authenticated user's current password.
     * Returns 200 (OK) if correct, throws exception / 400 Bad Request if invalid.
     */
    @PostMapping("/password")
    public ResponseEntity<Void> checkPassword(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody CheckPasswordRequest request) {
        authService.checkPassword(username, request);
        return ResponseEntity.ok().build();
    }
}
