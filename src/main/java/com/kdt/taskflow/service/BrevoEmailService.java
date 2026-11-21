package com.kdt.taskflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);

    @Value("${brevo.api-url:https://api.brevo.com/v3/smtp/email}")
    private String apiUrl;

    @Value("${brevo.api-key:}")
    private String apiKey;

    @Value("${brevo.sender.name:Taskflow}")
    private String senderName;

    @Value("${brevo.sender.email:kdttaskflow@gmail.com}")
    private String senderEmail;

    private final RestClient restClient;

    public BrevoEmailService() {
        this.restClient = RestClient.builder().build();
    }

    /**
     * Send password reset 6-digit code to the recipient using Brevo Transactional Email API.
     *
     * @param recipientEmail the email address of the recipient
     * @param code           the 6-digit verification code
     * @return the messageId returned by Brevo upon 201 Created
     */
    public String sendPasswordResetCode(String recipientEmail, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("Brevo API key is missing! Please configure BREVO_API_KEY in your .env file or environment variables.");
            throw new IllegalStateException("Email service is not configured (missing BREVO_API_KEY)");
        }

        String targetUrl = apiUrl;
        // If configured as generic https://brevo.com, route to the real transactional endpoint
        if ("https://brevo.com".equalsIgnoreCase(targetUrl) || "https://brevo.com/".equalsIgnoreCase(targetUrl)) {
            targetUrl = "https://api.brevo.com/v3/smtp/email";
        }

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of("email", recipientEmail)
                ),
                "subject", "Reset your Taskflow password",
                "htmlContent", "<p> The code is " + code + "</p>"
        );

        log.info("Sending password reset email to {} via Brevo URL {}", recipientEmail, targetUrl);

        try {
            ResponseEntity<Map<String, Object>> response = restClient.post()
                    .uri(targetUrl)
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("api-key", apiKey)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});

            Map<String, Object> body = response.getBody();
            String messageId = (body != null && body.get("messageId") != null)
                    ? body.get("messageId").toString()
                    : "success";

            log.info("Brevo email sent successfully with status {} and messageId: {}", response.getStatusCode(), messageId);
            return messageId;
        } catch (RestClientResponseException ex) {
            log.error("Brevo API call failed with status {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new IllegalStateException("Failed to send reset email: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Unexpected error while calling Brevo API: ", ex);
            throw new IllegalStateException("Failed to send reset email: " + ex.getMessage());
        }
    }
}

