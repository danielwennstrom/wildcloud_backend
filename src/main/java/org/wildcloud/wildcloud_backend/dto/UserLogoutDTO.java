package org.wildcloud.wildcloud_backend.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLogoutDTO {

    @Email
    @NotBlank(message = "You are not logged in")
    private String userEmail;


}

