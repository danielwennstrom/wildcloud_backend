package org.wildcloud.wildcloud_backend.request;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;

import java.util.List;

@Data
@Builder
public class DirectUploadRequest {
    private String userId;
    private String cameraId;
    private List<FileAdapter> files;
}
