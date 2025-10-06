package org.wildcloud.wildcloud_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.wildcloud.wildcloud_backend.config")
public class WildcloudBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WildcloudBackendApplication.class, args);
    }


}
