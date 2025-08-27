package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ImageUploadData {
    private byte[] buffer;
    private String filename;
    private String userId;
    private String cameraId;
    private String contentType;
    private String sourceType;
    private Map<String, Object> sourceMetadata;
    private LocalDateTime capturedAt;
}
