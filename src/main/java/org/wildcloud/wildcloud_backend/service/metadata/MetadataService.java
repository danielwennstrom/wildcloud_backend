package org.wildcloud.wildcloud_backend.service.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.service.metadata.extractor.MetadataExtractor;

import java.io.IOException;
import java.util.List;

/**
 * A service responsible for extracting metadata from the input file,
 * currently only supporting general files and images
 *
 * @see MetadataExtractor for implementations
 */
@Service
@RequiredArgsConstructor
public class MetadataService {
    private final List<MetadataExtractor<?>> extractors;

    public ImageMetadata extractImageMetadata(FileAdapter fileAdapter) throws IOException {
        return extract(fileAdapter, ImageMetadata.class);
    }

    public FileMetadata extractFileMetadata(FileAdapter fileAdapter) throws IOException {
        return extract(fileAdapter, FileMetadata.class);
    }

    private <T> T extract(FileAdapter fileAdapter, Class<T> metadataType) throws IOException {
        for (MetadataExtractor<?> extractor : extractors) {
            if (extractor.getMetadataType().equals(metadataType) && extractor.supports(fileAdapter)) {
                return metadataType.cast(extractor.extract(fileAdapter));
            }
        }

        throw new UnsupportedOperationException(
                "No extractor found for metadata type: " + metadataType.getSimpleName()
        );
    }
}
