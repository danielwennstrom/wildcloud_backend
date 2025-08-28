package org.wildcloud.wildcloud_backend.processor;

import org.wildcloud.wildcloud_backend.exception.ProcessException;
import org.wildcloud.wildcloud_backend.model.ImageUploadData;

import java.util.List;

public interface ImageProcessor {
    List<ImageUploadData> process(Object inputData) throws ProcessException;
}
