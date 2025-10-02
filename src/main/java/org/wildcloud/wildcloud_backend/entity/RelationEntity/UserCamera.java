package org.wildcloud.wildcloud_backend.entity.RelationEntity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_camera")
public class UserCamera {

    @Id
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("camera_id")
    private Long cameraId;
    @Column("role")
    private String role;
    @Column("camera_email")
    private String cameraEmail;



}
