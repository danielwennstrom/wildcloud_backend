package org.wildcloud.wildcloud_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Table(name = "camera_info")
public class CameraInfo {

    @Id
    @Column(unique = true, nullable = false, length = 100)
    @Email
    private String email;

    @ManyToMany(mappedBy = "cameras" )
    private Set<CameraInfo> cameras = new HashSet<>();





}
