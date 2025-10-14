package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.UploadSummaryDto;
import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.custom.ProcessException;
import org.wildcloud.wildcloud_backend.exception.custom.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import reactor.core.publisher.Mono;

public interface ImageUploadService {
    Mono<UploadSummaryDto> processUpload(SourceType sourceType, ImageUploadContext context) throws UploadException, ProcessException;
    Mono<Image> uploadSingleImage(ImageUploadData data);
}
