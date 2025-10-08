package org.wildcloud.wildcloud_backend.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("camera_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraInfo {

    @Id
    @Column
    private Long Id;

    @Email
    @NotNull
    @Column("camera_email")
    private String cameraEmail;


    @Column
    private String cameraName;


    @Override
    public String toString() {
        return "CameraInfo{" +
                "cameraId=" + Id +
                ", cameraEmail='" + cameraEmail + '\'' +
                ", cameraName='" + cameraName + '\'' +
                '}';
    }
}
