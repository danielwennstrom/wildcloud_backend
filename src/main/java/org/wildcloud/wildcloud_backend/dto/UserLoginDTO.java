package org.wildcloud.wildcloud_backend.dto;

import jakarta.validation.constraints.Email;

public class UserLoginDTO {

    @Email
    String email;
    String password;



}
