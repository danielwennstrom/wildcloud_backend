package org.wildcloud.wildcloud_backend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "wildcloud.retrieval")
public class ImageRetrievalConfig {
    private final int concurrencyLimit;

    @ConstructorBinding
    public ImageRetrievalConfig(int concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }
}
