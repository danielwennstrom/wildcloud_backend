package org.wildcloud.wildcloud_backend.dto;


import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.UserInfo;

import java.util.Set;

@Data
@Builder
public class CameraDTO {

    Long cameraId;
    String cameraName;
    String cameraEmail;
    Set<UserInfo> users;

}

