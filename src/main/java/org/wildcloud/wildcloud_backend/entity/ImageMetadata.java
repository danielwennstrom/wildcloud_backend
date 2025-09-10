package org.wildcloud.wildcloud_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private OffsetDateTime capturedAt;
    private OffsetDateTime lastModified;
    @OneToOne
    @JoinColumn(name = "image_entity_id")
    @JsonBackReference
    private ImageEntity imageEntity;
}
