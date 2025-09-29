package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.entity.ImageEntity;
import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;
import reactor.core.publisher.Mono;

public interface ImageUploadService {
    Mono<UploadResult> processUpload(String sourceType, Object inputData) throws UploadException, ProcessException;

    Mono<ImageEntity> uploadSingleImage(ImageUploadData data);

//    UploadResult uploadImages(List<ImageUploadData> imageData);

    void registerProcessor(String sourceType, ImageProcessor processor);

    void registerValidator(String beanName, ImageValidator validator);
}
