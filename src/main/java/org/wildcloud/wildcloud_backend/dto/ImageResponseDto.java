package org.wildcloud.wildcloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {
    private Long id;
    private String url;
    private String originalFilename;
    private String contentType;
    private Long size;
}