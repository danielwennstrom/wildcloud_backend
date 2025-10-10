package org.wildcloud.wildcloud_backend.model;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;

import java.util.List;

@Data
@Builder
public class ImageUploadContext {
    private Long cameraId;
    private List<FileAdapter> files;
}
