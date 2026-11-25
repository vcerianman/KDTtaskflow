package com.kdt.taskflow.dto;

public record MediaSignatureResponse(
        String signature,
        long timestamp,
        String apiKey,
        String uploadPreset,
        String folder
) {
}

