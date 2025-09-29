package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

import java.util.List;

@Data
@Builder
public class UploadResult {
    private List<ImageEntity> metadataList;
}
