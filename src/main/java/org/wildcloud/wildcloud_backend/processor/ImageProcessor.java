package org.wildcloud.wildcloud_backend.processor;

import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

public interface ImageProcessor {
    ImageUploadData process(Object inputData) throws ProcessException;
}
