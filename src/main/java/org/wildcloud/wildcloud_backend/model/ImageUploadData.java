package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ImageUploadData {
    private String userId;
    private String cameraId;
    private String sourceType;
    private Map<String, Object> sourceMetadata;
    private FileMetadata fileMetadata;
    private ImageMetadata imageMetadata;
}
