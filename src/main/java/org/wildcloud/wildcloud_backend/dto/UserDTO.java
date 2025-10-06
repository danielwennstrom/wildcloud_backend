package org.wildcloud.wildcloud_backend.dto;

import lombok.Builder;
import lombok.Data;
import org.wildcloud.wildcloud_backend.entity.CameraInfo;
import java.util.Set;

@Data
@Builder
public class UserDTO{

        Long id;
        String email;
        String firstName;
        String lastName;
        Long phoneNumber;
        Set<CameraInfo> cameras;
}



