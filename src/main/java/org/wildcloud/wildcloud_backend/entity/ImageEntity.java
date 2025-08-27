package org.wildcloud.wildcloud_backend.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

//@Entity
@Data
@Builder
public class ImageEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String cameraId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String sourceType;
    private OffsetDateTime capturedAt;
    private LocalDateTime uploadedAt;
    //    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(columnDefinition = "jsonb")
    private Map<String, Object> sourceMetadata;
}
