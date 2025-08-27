package org.wildcloud.wildcloud_backend.model;

import org.wildcloud.wildcloud_backend.exception.ProcessException;

public interface ImageProcessor {
    ImageUploadData process(Object inputData) throws ProcessException;
}
