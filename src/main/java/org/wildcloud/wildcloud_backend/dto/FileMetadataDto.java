package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadataDto {
    private String fileName;          // anonymized storage name
    private String originalFileName;  // original user-provided name
    private Long size;
    private String contentType;
}
