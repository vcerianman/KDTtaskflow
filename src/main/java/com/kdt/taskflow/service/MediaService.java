package com.kdt.taskflow.service;

import com.kdt.taskflow.dto.MediaDestroyRequest;
import com.kdt.taskflow.dto.MediaDestroyResponse;
import com.kdt.taskflow.dto.MediaSignatureRequest;
import com.kdt.taskflow.dto.MediaSignatureResponse;

public interface MediaService {

    /**
     * Generates a Cloudinary upload signature for direct client-side uploads.
     */
    MediaSignatureResponse generateSignature(MediaSignatureRequest request);

    /**
     * Destroys (deletes) an uploaded asset in Cloudinary and invalidates CDN cache.
     */
    MediaDestroyResponse destroyMedia(MediaDestroyRequest request);
}
