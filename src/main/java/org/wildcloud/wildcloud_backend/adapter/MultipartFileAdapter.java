package org.wildcloud.wildcloud_backend.adapter;

import org.springframework.web.multipart.MultipartFile;
import org.wildcloud.wildcloud_backend.domain.FileAdapter;

import java.io.IOException;

public record MultipartFileAdapter(MultipartFile file) implements FileAdapter {
    @Override
    public String getOriginalFilename() {
        return file.getOriginalFilename();
    }

    @Override
    public String getContentType() {
        return file.getContentType();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return file.getBytes();
    }

    @Override
    public long getSize() {
        return file.getSize();
    }
}
