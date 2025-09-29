package org.wildcloud.wildcloud_backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CameraRequestDTO {


    private Long cameraId;

    @NotBlank
    @Email(message = "Must be a valid email address")
    private String cameraEmail;

    @NotBlank(message = "Camera name is required")
    private String cameraName;


}
