package org.wildcloud.wildcloud_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String cameraId;
    private String sourceType;
    @OneToOne(mappedBy = "imageEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private FileMetadata fileMetadata;
    @OneToOne(mappedBy = "imageEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private ImageMetadata imageMetadata;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> sourceMetadata;
    private String storageKey;
}
