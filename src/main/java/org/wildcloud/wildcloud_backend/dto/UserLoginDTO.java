package org.wildcloud.wildcloud_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDTO {
    @Email
    @NotBlank(message = "Email is required")
    private String userEmail;
    @NotBlank(message = "Password is required")
    private String password;
}
