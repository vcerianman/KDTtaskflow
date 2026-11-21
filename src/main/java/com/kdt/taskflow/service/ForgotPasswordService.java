package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.ForgotPasswordRequest;
import com.kdt.taskflow.dto.ForgotPasswordResponse;

public interface ForgotPasswordService {

    /**
     * Handles forgot password workflow:
     * 1. Validate that username and email match in the database.
     * 2. Generate a 6-digit verification code.
     * 3. Cache the code with 5-minute expiration.
     * 4. Send email via Brevo API.
     */
    ForgotPasswordResponse processForgotPassword(ForgotPasswordRequest request);
}

