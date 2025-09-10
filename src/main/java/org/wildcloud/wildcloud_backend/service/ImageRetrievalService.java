package org.wildcloud.wildcloud_backend.service;

import org.wildcloud.wildcloud_backend.dto.ImageResponseDto;
import org.wildcloud.wildcloud_backend.entity.ImageEntity;

public interface ImageRetrievalService {
    ImageResponseDto retrieve(ImageEntity imageEntity);
}
