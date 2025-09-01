package org.wildcloud.wildcloud_backend.domain;

import java.io.IOException;

public interface FileAdapter {
    String getOriginalFilename();

    String getContentType();

    byte[] getBytes() throws IOException;

    long getSize();
}
