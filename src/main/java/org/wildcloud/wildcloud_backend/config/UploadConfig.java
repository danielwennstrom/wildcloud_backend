package org.wildcloud.wildcloud_backend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.util.unit.DataSize;

@Getter
@ConfigurationProperties(prefix = "wildcloud.upload")
public class UploadConfig {
    private final DataSize minFileSize;
    private final DataSize maxFileSize;

    @ConstructorBinding
    public UploadConfig(DataSize minFileSize, DataSize maxFileSize) {
        this.minFileSize = minFileSize;
        this.maxFileSize = maxFileSize;
    }
}
