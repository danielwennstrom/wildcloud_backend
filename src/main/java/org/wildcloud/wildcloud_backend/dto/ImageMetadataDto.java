package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ImageMetadataDto {
    private OffsetDateTime capturedAt;
    private OffsetDateTime lastModified;
}
