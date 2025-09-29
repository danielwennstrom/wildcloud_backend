package org.wildcloud.wildcloud_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDTO {

    @NotBlank
    @Email(message = "Must be a valid email address")
    private String userEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must contain at least 5 characters.")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private Long phoneNumber;
}