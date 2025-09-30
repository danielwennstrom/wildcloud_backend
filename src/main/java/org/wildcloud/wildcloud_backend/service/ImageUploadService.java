package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.entity.Image;
import org.wildcloud.wildcloud_backend.enums.SourceType;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadSummary;
import reactor.core.publisher.Mono;

public interface ImageUploadService {
    Mono<UploadSummary> processUpload(SourceType sourceType, Object inputData) throws UploadException, ProcessException;
    Mono<Image> uploadSingleImage(ImageUploadData data);
}
