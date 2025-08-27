package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

@Data
@Builder
public class UploadResult {
    private Long id;
    private String url;
    private ImageEntity metadata;
}
