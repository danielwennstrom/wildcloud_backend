package org.wildcloud.wildcloud_backend.adapter;

import org.wildcloud.wildcloud_backend.domain.FileAdapter;

public record FilePartAdapter(
        String filename,
        String contentType,
        byte[] bytes,
        long size
) implements FileAdapter {

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public long getSize() {
        return size;
    }
}

