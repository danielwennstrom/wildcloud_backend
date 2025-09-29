package org.wildcloud.wildcloud_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Data
@Builder
@Table("image_metadata")
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadata {
    @Id
    private Long id;
    @Column("captured_at")
    private OffsetDateTime capturedAt;
    @Column("last_modified")
    private OffsetDateTime lastModified;
    @Column("image_entity_id")
    private Long imageEntityId;
}
