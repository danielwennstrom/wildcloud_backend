package org.wildcloud.wildcloud_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WildcloudBackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().systemProperties().ignoreIfMissing().directory("src/main/java/org/wildcloud/wildcloud_backend/config").load();
        
        SpringApplication.run(WildcloudBackendApplication.class, args);
    }


}
