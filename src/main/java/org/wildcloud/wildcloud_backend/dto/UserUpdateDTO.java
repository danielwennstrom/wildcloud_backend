package org.wildcloud.wildcloud_backend.dto;

public class UserUpdateDTO extends UserRegistrationDTO{


    UserUpdateDTO(

            Long userId,

            String cameraEmail,
            String firstName,
            String lastName,
            String phoneNumber,
            String email,
            String password) {
            super(cameraEmail, firstName, lastName, phoneNumber, email, password);
    }
}
