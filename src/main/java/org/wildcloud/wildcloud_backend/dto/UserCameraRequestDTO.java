package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCameraRequestDTO {
    String userEmail;
    String cameraEmail;
}
