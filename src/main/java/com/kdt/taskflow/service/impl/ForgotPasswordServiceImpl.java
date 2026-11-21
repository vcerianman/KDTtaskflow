package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.domain.User;
import com.kdt.taskflow.dto.ForgotPasswordRequest;
import com.kdt.taskflow.dto.ForgotPasswordResponse;
import com.kdt.taskflow.mapper.UserMapper;
import com.kdt.taskflow.service.BrevoEmailService;
import com.kdt.taskflow.service.ForgotPasswordService;
import com.kdt.taskflow.service.PasswordResetCodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordServiceImpl.class);

    private static final long CODE_EXPIRATION_SECONDS = 300L; // 5 minutes

    private final UserMapper userMapper;
    private final PasswordResetCodeCache codeCache;
    private final BrevoEmailService brevoEmailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public ForgotPasswordServiceImpl(UserMapper userMapper,
                                     PasswordResetCodeCache codeCache,
                                     BrevoEmailService brevoEmailService,
                                     PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.codeCache = codeCache;
        this.brevoEmailService = brevoEmailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ForgotPasswordResponse processForgotPassword(ForgotPasswordRequest request) {
        String username = request.username().trim();
        String email = request.email().trim();

        // 1. Verify that the user exists and the email matches
        User user = userMapper.findByUsername(username)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> {
                    log.warn("Forgot password failed: user '{}' not found", username);
                    return new IllegalArgumentException("Username and email do not match");
                });

        if (user.getEmail() == null || !user.getEmail().trim().equalsIgnoreCase(email)) {
            log.warn("Forgot password failed: email '{}' does not match for user '{}'", email, username);
            throw new IllegalArgumentException("Username and email do not match");
        }

        boolean hasCode = request.code() != null && !request.code().trim().isBlank();
        boolean hasNewPassword = request.newPassword() != null && !request.newPassword().isBlank();

        // Case 3: Both code and new_password are provided -> Update password in database
        if (hasCode && hasNewPassword) {
            String inputCode = request.code().trim();
            Optional<String> cachedCode = codeCache.getCode(user.getUsername());

            if (cachedCode.isEmpty() || !cachedCode.get().equals(inputCode)) {
                log.warn("Verification code mismatch or expired for user '{}'", username);
                throw new IllegalArgumentException("Verification code does not match or has expired");
            }

            String rawPassword = request.newPassword().trim();
            if (rawPassword.isBlank()) {
                throw new IllegalArgumentException("New password cannot be blank");
            }

            // Convert new_password to BCrypt hash token and replace in database
            String encodedPassword = passwordEncoder.encode(rawPassword);
            userMapper.updatePassword(user.getId(), encodedPassword);

            // Invalidate/consume code from cache
            codeCache.remove(user.getUsername());

            log.info("Password successfully updated for user '{}'", username);
            return ForgotPasswordResponse.passwordUpdated();
        }

        // Case 2: Code is provided, but not new_password -> Verify code and keep in cache
        if (hasCode) {
            String inputCode = request.code().trim();
            Optional<String> cachedCode = codeCache.getCode(user.getUsername());

            if (cachedCode.isEmpty() || !cachedCode.get().equals(inputCode)) {
                log.warn("Verification code mismatch or expired for user '{}'", username);
                throw new IllegalArgumentException("Verification code does not match or has expired");
            }

            log.info("Verification code verified successfully for user '{}' (kept in cache)", username);
            return ForgotPasswordResponse.codeValid();
        }

        // If new_password was provided without a code, reject
        if (hasNewPassword) {
            throw new IllegalArgumentException("Verification code is required to set a new password");
        }

        // Case 1: Neither code nor new_password provided -> Check 45s cooldown, generate code, cache for 5 min, send email
        long cooldownRemaining = codeCache.getCooldownRemaining(user.getUsername());
        if (cooldownRemaining > 0) {
            log.warn("Forgot password rate-limited for user '{}': {}s cooldown remaining", username, cooldownRemaining);
            throw new IllegalArgumentException("Please wait " + cooldownRemaining + " seconds before requesting another code");
        }

        int codeInt = 100000 + secureRandom.nextInt(900000);
        String code = String.valueOf(codeInt);

        codeCache.saveCode(user.getUsername(), code, CODE_EXPIRATION_SECONDS);

        String messageId = brevoEmailService.sendPasswordResetCode(user.getEmail(), code);

        return ForgotPasswordResponse.emailSent(messageId);
    }
}
