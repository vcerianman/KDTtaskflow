package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;
import com.kdt.taskflow.service.MediaService;
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
}

