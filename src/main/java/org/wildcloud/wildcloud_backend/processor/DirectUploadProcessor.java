package org.wildcloud.wildcloud_backend.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.model.FileMetadata;
import org.wildcloud.wildcloud_backend.model.ImageMetadata;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.request.DirectUploadRequest;
import org.wildcloud.wildcloud_backend.service.MetadataExtractorService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DirectUploadProcessor implements ImageProcessor {
    private final MetadataExtractorService metadataExtractorService;

    @Override
    public ImageUploadData process(Object inputData) throws ProcessException {
        if (!(inputData instanceof DirectUploadRequest request)) {
            throw new ProcessException("Invalid input data for direct upload");
        }

        try {
            MultipartFile file = request.getFile();

            FileMetadata fileMetadata = metadataExtractorService.extractFileMetadata(file);
            ImageMetadata imageMetadata = metadataExtractorService.extractImageMetadata(file);

            return ImageUploadData.builder()
                    .userId(request.getUserId())
                    .cameraId(request.getCameraId())
                    .sourceType("direct")
                    .imageMetadata(imageMetadata)
                    .fileMetadata(fileMetadata)
                    .sourceMetadata(Map.of(
                            "uploadedVia", "web"))
                    .build();
        } catch (Exception e) {
            throw new ProcessException("Failed to process direct upload", e);
        }
    }
}
