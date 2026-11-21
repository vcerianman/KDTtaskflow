package com.kdt.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ForgotPasswordResponse(
        String message,
        String messageId
) {
    public static ForgotPasswordResponse emailSent(String messageId) {
        return new ForgotPasswordResponse("Verification code sent successfully", messageId);
    }

    public static ForgotPasswordResponse codeValid() {
        return new ForgotPasswordResponse("Verification code is valid", null);
    }

    public static ForgotPasswordResponse passwordUpdated() {
        return new ForgotPasswordResponse("Password updated successfully", null);
    }
}
