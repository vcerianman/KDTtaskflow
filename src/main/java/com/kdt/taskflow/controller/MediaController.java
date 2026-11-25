package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.MediaDestroyRequest;
import com.kdt.taskflow.dto.MediaDestroyResponse;
import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;
import com.kdt.taskflow.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * POST /api/media/signature — Generate a Cloudinary signed upload signature.
     */
    @PostMapping("/signature")
    public ResponseEntity<MediaSignatureResponse> generateSignature(
            @RequestBody(required = false) MediaSignatureRequest request) {
        return ResponseEntity.ok(mediaService.generateSignature(request));
    }

    /**
     * POST /api/media/destroy — Delete an uploaded asset on Cloudinary and invalidate CDN cache.
     */
    @PostMapping("/destroy")
    public ResponseEntity<MediaDestroyResponse> destroyMedia(
            @Valid @RequestBody MediaDestroyRequest request) {
        return ResponseEntity.ok(mediaService.destroyMedia(request));
    }
}
