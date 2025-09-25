package org.wildcloud.wildcloud_backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.repository.UserRepository;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyCommandLineRunner.class);
    private final UserRepository userRepository;

    public MyCommandLineRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting application, and some tests...");



    }





}
