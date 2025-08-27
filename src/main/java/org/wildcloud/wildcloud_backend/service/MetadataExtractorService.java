package org.wildcloud.wildcloud_backend.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.model.FileMetadata;
import org.wildcloud.wildcloud_backend.model.ImageMetadata;
import org.wildcloud.wildcloud_backend.util.ExifUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class MetadataExtractorService {
    private final ExifUtils exifUtils;

    public MetadataExtractorService(ExifUtils exifUtils) {
        this.exifUtils = exifUtils;
    }

    private static String getAnonymizedName(String name) {
        String extension = FilenameUtils.getExtension(name);
        if (extension.isBlank()) {
            return UUID.randomUUID().toString();
        } else {
            return UUID.randomUUID() + "." + extension;
        }
    }

    public ImageMetadata extractImageMetadata(MultipartFile multipartFile) throws IOException {
        byte[] fileBytes = multipartFile.getBytes();

        OffsetDateTime capturedAt;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            capturedAt = exifUtils.getOriginalOffsetDateTime(is);
        } catch (Exception e) {
            capturedAt = null;
        }

        OffsetDateTime lastModified;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            lastModified = exifUtils.getModifiedOffsetDateTime(is);
        } catch (Exception e) {
            lastModified = null;
        }

        return ImageMetadata.builder()
                .lastModified(lastModified)
                .capturedAt(capturedAt)
                .build();
    }

    public FileMetadata extractFileMetadata(MultipartFile multipartFile) throws IOException {
        byte[] fileBytes = multipartFile.getBytes();

        String originalName = multipartFile.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "file";
        }
        String anonymizedName = getAnonymizedName(originalName);

        return FileMetadata.builder()
                .fileName(anonymizedName)
                .originalFilename(originalName)
                .size((long) fileBytes.length)
                .buffer(fileBytes)
                .contentType(multipartFile.getContentType())
                .build();
    }
}

