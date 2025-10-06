package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.custom.ProcessException;
import org.wildcloud.wildcloud_backend.exception.custom.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadContext;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadSummary;
import reactor.core.publisher.Mono;

public interface ImageUploadService {
    Mono<UploadSummary> processUpload(SourceType sourceType, ImageUploadContext context) throws UploadException, ProcessException;
    Mono<Image> uploadSingleImage(ImageUploadData data);
}
