package org.wildcloud.wildcloud_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.awt.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cameraInfo")
@Entity
@Validated
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "camera_email")
    private CameraInfo cameraInfo;


    @Column
    private String firstName;
    @Column
    private String lastName;

    @Column
    private Long phoneNumber;

    @Column
    @Email
    private String email;

    @Column
    @Email
    private String cameraEmail; //Email för kameror som användaren har tillgång till.


    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "image_content_type")
    private String imageContentType; //Typ vad är det i bilden, är det rådjur, älg, löv osv? För AI senare?

    @Column(name = "image_filename")
    private String imageFilename;


    @Column
    private String password;






}
