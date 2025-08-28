package org.wildcloud.wildcloud_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@Table(name = "user_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cameraInfo")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "user_camera",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "camera_email")
    )
    private Set<CameraInfo> cameras = new HashSet<>();


    @Column
    private String firstName;
    @Column
    private String lastName;

    @Column
    private Long phoneNumber;

    @Column(name = "user_email", unique = true, nullable = false, length = 100)
    @Email
    private String email;

    //TODO:
    @Column
    @Transient
    @Email
    private String cameraEmail; //Email för kameror som användaren har tillgång till.




    @Column(nullable = false)
    private String password;






}
