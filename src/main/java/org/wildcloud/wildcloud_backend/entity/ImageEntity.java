package org.wildcloud.wildcloud_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {
    @Id
    private Long id;
    @Column("user_id")
    private String userId;
    @Column("camera_id")
    private String cameraId;
    @Column("source_type")
    private String sourceType;
    @JsonManagedReference
    private FileMetadata fileMetadata;
    @JsonManagedReference
    private ImageMetadata imageMetadata;
    @Column("source_metadata")
    private String sourceMetadata;
    private String storageKey;
}
