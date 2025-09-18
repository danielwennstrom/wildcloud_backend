package org.wildcloud.wildcloud_backend.service.metadata.extractor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.util.ExifUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ImageMetadataExtractor implements MetadataExtractor<ImageMetadata> {
    private final ExifUtils exifUtils;

    @Override
    public boolean supports(FileAdapter fileAdapter) {
        String contentType = fileAdapter.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @Override
    public ImageMetadata extract(FileAdapter fileAdapter) throws IOException {
        byte[] fileBytes = fileAdapter.getBytes();

        OffsetDateTime capturedAt;
        OffsetDateTime lastModified;
        try (InputStream is1 = new ByteArrayInputStream(fileBytes);
             InputStream is2 = new ByteArrayInputStream(fileBytes)) {
            capturedAt = exifUtils.getOriginalOffsetDateTime(is1);
            lastModified = exifUtils.getModifiedOffsetDateTime(is2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ImageMetadata.builder()
                .lastModified(lastModified)
                .capturedAt(capturedAt)
                .build();
    }

    @Override
    public Class<ImageMetadata> getMetadataType() {
        return ImageMetadata.class;
    }
}


