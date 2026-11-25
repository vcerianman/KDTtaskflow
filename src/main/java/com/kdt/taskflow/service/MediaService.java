package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;

public interface MediaService {

    /**
     * Generates a Cloudinary upload signature for direct client-side uploads.
     */
    MediaSignatureResponse generateSignature(MediaSignatureRequest request);
}

