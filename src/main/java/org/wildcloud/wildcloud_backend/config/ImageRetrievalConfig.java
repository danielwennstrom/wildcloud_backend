package org.wildcloud.wildcloud_backend.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "wildcloud.retrieval")
public class ImageRetrievalConfig {
    private final String baseUrl;
    private final int concurrencyLimit;

    @ConstructorBinding
    public ImageRetrievalConfig(String baseUrl, int concurrencyLimit) {
        this.baseUrl = baseUrl;
        this.concurrencyLimit = concurrencyLimit;
    }
}
