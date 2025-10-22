package org.wildcloud.wildcloud_backend.service.metadata.extractor;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileMetadataExtractor implements MetadataExtractor<FileMetadata> {
    @Override
    public boolean supports(FileAdapter fileAdapter) {
        return true;
    }

    @Override
    public FileMetadata extract(FileAdapter fileAdapter) throws IOException {
        String originalName = fileAdapter.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "file";
        }
        String anonymizedName = getAnonymizedName(originalName);

        return FileMetadata.builder()
                .fileName(anonymizedName)
                .originalFileName(originalName)
                .size((long) fileAdapter.getBytes().length)
                .contentType(fileAdapter.getContentType())
                .build();
    }

    @Override
    public Class<FileMetadata> getMetadataType() {
        return FileMetadata.class;
    }

    private String getAnonymizedName(String name) {
        String extension = FilenameUtils.getExtension(name);
        if (extension.isBlank()) {
            return UUID.randomUUID().toString();
        } else {
            return UUID.randomUUID() + "." + extension;
        }
    }
}
