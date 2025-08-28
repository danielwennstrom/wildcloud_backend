package org.wildcloud.wildcloud_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;          // anonymized storage name
    private String originalFileName;  // original user-provided name
    private Long size;
    private String contentType;
    @OneToOne
    @JoinColumn(name = "image_entity_id")
    @JsonBackReference
    private ImageEntity imageEntity;
}
