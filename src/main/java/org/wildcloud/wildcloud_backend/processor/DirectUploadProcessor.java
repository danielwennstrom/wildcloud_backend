package org.wildcloud.wildcloud_backend.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.custom.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.service.metadata.MetadataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectUploadProcessor implements ImageProcessor {
    private final MetadataService metadataService;

    @Override
    public List<ImageUploadData> process(ImageUploadContext context) throws ProcessException {
        List<ImageUploadData> result = new ArrayList<>();

        try {
            for (FileAdapter file : context.getFiles()) {
                log.debug("Processing file: name={}, size={} bytes",
                        file.getOriginalFilename(), file.getSize());

                FileMetadata fileMetadata = metadataService.extractFileMetadata(file);
                ImageMetadata imageMetadata = metadataService.extractImageMetadata(file);

                // todo: ha med ett uploadedVia-fält i context DTO:n för webb/app?
                ImageUploadData imageData = ImageUploadData.builder()
                        .cameraId(context.getCameraId())
                        .sourceType(this.getSourceType().name().toLowerCase())
                        .buffer(file.getBytes())
                        .imageMetadata(imageMetadata)
                        .fileMetadata(fileMetadata)
                        .sourceMetadata(Map.of(
                                "uploadedVia", "web"))
                        .build();

                result.add(imageData);
                log.info("File processed: name={}, size={}",
                        file.getOriginalFilename(),
                        file.getSize());
            }

            return result;
        } catch (Exception e) {
            throw new ProcessException("Failed to process direct upload", e);
        }
    }

    @Override
    public SourceType getSourceType() {
        return SourceType.DIRECT;
    }
}
