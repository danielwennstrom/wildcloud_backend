package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.processor.DirectUploadProcessor;

import java.util.Map;

/**
 * The sourceMetadata field can be used for anything, but is primarily meant to specify the source of the image
 *
 * @see DirectUploadProcessor specifies that the image was uploaded via the web, for example
 */
@Data
@Builder
public class ImageUploadData {
    private Long cameraId;
    private String sourceType;
    private byte[] buffer;
    private Map<String, Object> sourceMetadata;
    private FileMetadata fileMetadata;
    private ImageMetadata imageMetadata;
}
