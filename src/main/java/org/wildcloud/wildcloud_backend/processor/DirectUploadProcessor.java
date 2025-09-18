package org.wildcloud.wildcloud_backend.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;
import org.wildcloud.wildcloud_backend.entity.FileMetadata;
import org.wildcloud.wildcloud_backend.entity.ImageMetadata;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
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
    public List<ImageUploadData> process(Object inputData) throws ProcessException {
        if (!(inputData instanceof DirectUploadRequest request)) {
            throw new ProcessException("Invalid input data for direct upload");
        }

        List<ImageUploadData> result = new ArrayList<>();

        try {
            for (FileAdapter file : request.getFiles()) {
                log.debug("Processing file: name={}, size={} bytes",
                        file.getOriginalFilename(), file.getSize());

                FileMetadata fileMetadata = metadataService.extractFileMetadata(file);
                ImageMetadata imageMetadata = metadataService.extractImageMetadata(file);

                ImageUploadData imageData = ImageUploadData.builder()
                        .userId(request.getUserId())
                        .cameraId(request.getCameraId())
                        .sourceType("direct")
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
}
