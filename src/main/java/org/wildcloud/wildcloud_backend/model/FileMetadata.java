package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMetadata {
    private String fileName;          // anonymized storage name
    private String originalFileName;  // original user-provided name
    private Long size;
    private String contentType;
    private byte[] buffer;
}
