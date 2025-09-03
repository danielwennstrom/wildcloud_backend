package org.wildcloud.wildcloud_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "wildcloud.cloudflare.r2")
@Data
@Component
public class R2Properties {
    private String accountId;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String endpoint;
    // TODO: implementera worker i cloudflare
    private boolean useCustomDomain;
    private String customDomain;
}
