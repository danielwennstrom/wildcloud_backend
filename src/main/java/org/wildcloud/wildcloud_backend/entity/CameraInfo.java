package org.wildcloud.wildcloud_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.validation.annotation.Validated;

@Data
@Table("camera_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CameraInfo {

    @Id
    @Column("camera_email")
    private String cameraEmail;

    @Column
    private Long cameraId;

    @Column
    private Long userId;


}
