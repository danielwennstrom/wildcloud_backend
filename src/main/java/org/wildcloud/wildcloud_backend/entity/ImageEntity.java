package org.wildcloud.wildcloud_backend.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.wildcloud.wildcloud_backend.model.ImageMetadata;

import java.time.LocalDateTime;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
//@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageEntity extends ImageMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String cameraId;
    private String filename;
    private Long fileSize;
    private String contentType;
    private String sourceType;
    private LocalDateTime capturedAt;
    private LocalDateTime uploadedAt;
    //    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "jsonb")
    private Map<String, Object> sourceMetadata;

    @Embedded
    private ImageMetadata imageMetadata;
}
