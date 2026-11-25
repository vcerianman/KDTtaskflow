package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;
import com.kdt.taskflow.service.MediaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${cloudinary.upload-preset:}")
    private String uploadPreset;

    @Override
    public MediaSignatureResponse generateSignature(MediaSignatureRequest request) {
        if (apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary API credentials are not configured (missing CLOUDINARY_API_KEY or CLOUDINARY_API_SECRET)");
        }

        long timestamp = Instant.now().getEpochSecond();
        String folder = (request != null && request.folder() != null && !request.folder().isBlank())
                ? request.folder().trim()
                : null;
        String effectiveUploadPreset = (uploadPreset != null && !uploadPreset.isBlank())
                ? uploadPreset.trim()
                : null;

        // Cloudinary requires parameters (excluding file, cloud_name, resource_type, api_key)
        // to be sorted alphabetically by key before appending the API secret.
        // Dynamic folder mode uses "asset_folder" in the upload signature.
        Map<String, String> paramsToSign = new TreeMap<>();
        if (folder != null) {
            paramsToSign.put("asset_folder", folder);
        }
        paramsToSign.put("timestamp", String.valueOf(timestamp));
        if (effectiveUploadPreset != null) {
            paramsToSign.put("upload_preset", effectiveUploadPreset);
        }

        String paramString = paramsToSign.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String signature = sha1Hex(paramString + apiSecret.trim());

        return new MediaSignatureResponse(
                signature,
                timestamp,
                apiKey.trim(),
                effectiveUploadPreset,
                folder
        );
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm not available", e);
        }
    }
}

