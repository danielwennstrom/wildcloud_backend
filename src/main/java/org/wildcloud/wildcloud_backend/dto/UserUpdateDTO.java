package org.wildcloud.wildcloud_backend.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;


public class UserUpdateDTO extends UserRegistrationDTO {


    UserUpdateDTO(

            Long id,

            String firstName,
            String lastName,
            Long phoneNumber,

            @Email
            String email,
            String password) {
        super(id, firstName, lastName, phoneNumber, email, password);
    }

}
