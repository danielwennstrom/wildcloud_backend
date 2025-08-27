package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ImageMetadata {
    private OffsetDateTime capturedAt;
    private OffsetDateTime lastModified;
}
