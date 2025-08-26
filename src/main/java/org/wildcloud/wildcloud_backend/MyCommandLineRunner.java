package org.wildcloud.wildcloud_backend;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.wildcloud.wildcloud_backend.entity.UserInfo;
import org.wildcloud.wildcloud_backend.repository.UserRepo;

import java.util.SortedSet;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyCommandLineRunner.class);
    private final UserRepo userRepo;

    public MyCommandLineRunner(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting application, and some tests...");

        UserInfo newUser = UserInfo.builder()
                .email("test1@test.se")
                .password("admin1")
                .firstName("Test")
                .lastName("User")
                .phoneNumber(1234567890L)
                .cameraEmail("cameraemail@test.se")
                .build();

        if (userRepo.findByEmail(newUser.getEmail()).isEmpty()) {
            UserInfo savedUser = userRepo.save(newUser); // Save the user to the database
            logger.info("User added successfully: {}", savedUser);
        } else {
            logger.warn("User with email {} already exists!", newUser.getEmail());
        }


        logger.info("Current users in the database:");
        userRepo.findAll().forEach(user -> logger.info(user.toString()));
        userRepo.findById(1L);
        logger.info("Tests completed.");

    }





}
