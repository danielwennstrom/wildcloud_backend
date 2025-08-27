package org.wildcloud.wildcloud_backend.processor;

import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageProcessor;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Component
public class DirectUploadProcessor implements ImageProcessor {
    @Override
    public ImageUploadData process(Object inputData) throws ProcessException {
        if (!(inputData instanceof DirectUploadRequest request)) {
            throw new ProcessException("Invalid input data for direct upload");
        }

        try {
            return ImageUploadData.builder()
                    .buffer(request.getFile().getBytes())
                    .filename(request.getFile().getOriginalFilename())
                    .userId(request.getUserId())
                    .cameraId(request.getCameraId())
                    .contentType(request.getFile().getContentType())
                    .sourceType("direct")
                    .capturedAt(LocalDateTime.now())
                    .sourceMetadata(Map.of(
                            "uploadedVia", "web",
                            "originalFilename", Objects.requireNonNull(request.getFile().getOriginalFilename())
                    ))
                    .build();
        } catch (Exception e) {
            throw new ProcessException("Failed to process direct upload", e);
        }
    }
}
