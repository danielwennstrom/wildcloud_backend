package org.wildcloud.wildcloud_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.awt.*;


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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "camera_email", referencedColumnName = "email")
//    private CameraInfo cameraInfo;


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


    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "image_content_type" ,length = 50)
    private String imageContentType; //Typ vad är det i bilden, är det rådjur, älg, löv osv? För AI senare?

    @Column(name = "image_filename", length = 50)
    private String imageFilename;


    @Column(nullable = false)
    private String password;






}
