package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.UploadResult;
import org.wildcloud.wildcloud_backend.processor.ImageProcessor;
import org.wildcloud.wildcloud_backend.validator.ImageValidator;

public interface ImageUploadService {
    UploadResult processUpload(String sourceType, Object inputData) throws UploadException;

    UploadResult uploadImage(ImageUploadData imageData);

    void registerProcessor(String sourceType, ImageProcessor processor);

    void registerValidator(String beanName, ImageValidator validator);
}
