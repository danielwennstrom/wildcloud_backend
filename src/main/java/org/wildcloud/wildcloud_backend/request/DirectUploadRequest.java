package org.wildcloud.wildcloud_backend.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class DirectUploadRequest {
    private String userId;
    private String cameraId;
    private MultipartFile file;
}
