package org.wildcloud.wildcloud_backend.service.metadata.extractor;

import org.wildcloud.wildcloud_backend.domain.FileAdapter;

import java.io.IOException;

/**
 * Implementations of this interface are responsible for a single kind of metadata extraction
 * Designed to be easy to extend with new capabilities, e.g. maybe for video, down the line
 *
 * @param <T> The type of metadata object this extractor extracts (ImageMetadata, etc.)
 */
public interface MetadataExtractor<T> {
    boolean supports(FileAdapter fileAdapter);

    T extract(FileAdapter fileAdapter) throws IOException;

    Class<T> getMetadataType();
}
