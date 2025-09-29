package org.wildcloud.wildcloud_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("file_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    @Id
    private Long id;
    @Column("file_name")
    private String fileName;          // anonymized storage name
    @Column("original_file_name")
    private String originalFileName;  // original user-provided name
    private Long size;
    @Column("content_type")
    private String contentType;
    @Column("image_entity_id")
    private Long imageEntityId;
}
