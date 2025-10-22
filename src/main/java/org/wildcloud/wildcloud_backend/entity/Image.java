package org.wildcloud.wildcloud_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    private Long id;
    @Column("camera_id")
    private Long cameraId;
    @Column("source_type")
    private String sourceType;
    @JsonManagedReference
    @Transient
    private FileMetadata fileMetadata;
    @JsonManagedReference
    @Transient
    private ImageMetadata imageMetadata;
    @Column("source_metadata")
    private String sourceMetadata;
    private String storageKey;
}
