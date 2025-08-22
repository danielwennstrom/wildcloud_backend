package org.wildcloud.wildcloud_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDTO(

        Long userId,

        @Email
        String cameraEmail,

        String firstName,
        String lastName,
        Long phoneNumber,

        @NotBlank
        @NotNull @Email @Size(min = 2, max = 200)
        String email)

{
}
