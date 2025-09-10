package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;

import java.util.Map;

@Data
@Builder
public class ImageUploadData {
    private String userId;
    private String cameraId;
    private String sourceType;
    private byte[] buffer;
    private Map<String, Object> sourceMetadata;
    private FileMetadata fileMetadata;
    private ImageMetadata imageMetadata;
}
