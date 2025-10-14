package org.wildcloud.wildcloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDto {
    private Long id;
    private String url;
    private FileMetadataDto fileMetadata;
    private ImageMetadataDto imageMetadata;
}