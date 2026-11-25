package com.kdt.taskflow.service.impl;

import com.kdt.taskflow.dto.MediaDestroyRequest;
import com.kdt.taskflow.dto.MediaDestroyResponse;
import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;
import com.kdt.taskflow.service.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {

    private static final Logger log = LoggerFactory.getLogger(MediaServiceImpl.class);
    private static final Set<String> ALLOWED_RESOURCE_TYPES = Set.of("image", "video", "raw");

    @Value("${cloudinary.cloud-name:bs5wxybt}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${cloudinary.upload-preset:}")
    private String uploadPreset;

    private final RestClient restClient;

    public MediaServiceImpl() {
        this.restClient = RestClient.builder().build();
    }

    @Override
    public MediaSignatureResponse generateSignature(MediaSignatureRequest request) {
        requireCredentials();

        long timestamp = Instant.now().getEpochSecond();
        String folder = (request != null && request.folder() != null && !request.folder().isBlank())
                ? request.folder().trim()
                : null;
        String effectiveUploadPreset = (uploadPreset != null && !uploadPreset.isBlank())
                ? uploadPreset.trim()
                : null;

        // Dynamic folder mode uses "asset_folder" in the upload signature.
        Map<String, String> paramsToSign = new TreeMap<>();
        if (folder != null) {
            paramsToSign.put("asset_folder", folder);
        }
        paramsToSign.put("timestamp", String.valueOf(timestamp));
        if (effectiveUploadPreset != null) {
            paramsToSign.put("upload_preset", effectiveUploadPreset);
        }

        String signature = signParams(paramsToSign);

        return new MediaSignatureResponse(
                signature,
                timestamp,
                apiKey.trim(),
                effectiveUploadPreset,
                folder
        );
    }

    @Override
    public MediaDestroyResponse destroyMedia(MediaDestroyRequest request) {
        requireCredentials();

        String publicId = request.publicId().trim();
        String resourceType = (request.resourceType() != null && !request.resourceType().isBlank())
                ? request.resourceType().trim().toLowerCase()
                : "image";

        if (!ALLOWED_RESOURCE_TYPES.contains(resourceType)) {
            throw new IllegalArgumentException("Invalid resourceType '" + resourceType + "'. Allowed values: image, video, raw");
        }

        long timestamp = Instant.now().getEpochSecond();
        String timestampStr = String.valueOf(timestamp);

        Map<String, String> paramsToSign = new TreeMap<>();
        paramsToSign.put("invalidate", "true");
        paramsToSign.put("public_id", publicId);
        paramsToSign.put("timestamp", timestampStr);

        String signature = signParams(paramsToSign);

        MultiValueMap<String, String> formBody = new LinkedMultiValueMap<>();
        formBody.add("public_id", publicId);
        formBody.add("timestamp", timestampStr);
        formBody.add("api_key", apiKey.trim());
        formBody.add("signature", signature);
        formBody.add("invalidate", "true");

        String destroyUrl = String.format("https://api.cloudinary.com/v1_1/%s/%s/destroy",
                cloudName.trim(), resourceType);

        log.info("Destroying Cloudinary {} asset '{}' via {}", resourceType, publicId, destroyUrl);

        try {
            ResponseEntity<Map<String, Object>> response = restClient.post()
                    .uri(destroyUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formBody)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});

            Map<String, Object> body = response.getBody();
            String result = (body != null && body.get("result") != null)
                    ? body.get("result").toString()
                    : "ok";

            return new MediaDestroyResponse(result, publicId);
        } catch (RestClientResponseException ex) {
            log.error("Cloudinary destroy failed with status {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new IllegalStateException("Failed to destroy Cloudinary media: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Unexpected error while calling Cloudinary destroy API", ex);
            throw new IllegalStateException("Failed to destroy Cloudinary media: " + ex.getMessage());
        }
    }

    private void requireCredentials() {
        if (apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary API credentials are not configured (missing CLOUDINARY_API_KEY or CLOUDINARY_API_SECRET)");
        }
    }

    /**
     * Cloudinary signs all body parameters (excluding file, api_key, signature, resource_type)
     * sorted alphabetically by key and joined with '&', followed directly by api_secret.
     */
    private String signParams(Map<String, String> sortedParams) {
        String paramString = sortedParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        return sha1Hex(paramString + apiSecret.trim());
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
