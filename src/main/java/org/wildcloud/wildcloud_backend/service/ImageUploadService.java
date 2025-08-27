package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.exception.UploadException;
import org.wildcloud.wildcloud_backend.model.ImageProcessor;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;
import org.wildcloud.wildcloud_backend.model.ImageValidator;
import org.wildcloud.wildcloud_backend.model.UploadResult;

public interface ImageUploadService {
    UploadResult processUpload(String sourceType, Object inputData) throws UploadException;

    UploadResult uploadImage(ImageUploadData imageData);

    void registerProcessor(String sourceType, ImageProcessor processor);

    void registerValidator(String beanName, ImageValidator validator);
}
