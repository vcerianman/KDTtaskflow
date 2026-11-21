package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.ForgotPasswordRequest;
import com.kdt.taskflow.dto.ForgotPasswordResponse;
import com.kdt.taskflow.service.ForgotPasswordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public controller for password recovery.
 * POST /api/forgotpassword
 */
@RestController
@RequestMapping("/api")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    /**
     * POST /api/forgotpassword — Public endpoint.
     * Receives username & email, validates association, generates 6-digit code,
     * stores in cache for 5 minutes, and sends via Brevo API.
     */
    @PostMapping({"/forgotpassword", "/forgot-password", "/auth/forgotpassword", "/auth/forgot-password"})
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = forgotPasswordService.processForgotPassword(request);
        return ResponseEntity.ok(response);
    }
}

