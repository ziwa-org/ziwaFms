package com.example.ziwa.config;

import com.example.ziwa.model.AppUser;
import com.example.ziwa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin user if no users exist
        if (userRepository.count() == 0) {
            log.info("No users found. Creating default admin user...");
            
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .fullName("System Administrator")
                    .role(AppUser.UserRole.ADMIN)
                    .active(true)
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin user created successfully!");
            log.info("Username: admin");
            log.info("Password: Admin@123");
            
            // Create a test user
            AppUser testUser = AppUser.builder()
                    .username("testuser")
                    .passwordHash(passwordEncoder.encode("Test@123"))
                    .fullName("Test User")
                    .role(AppUser.UserRole.USER)
                    .active(true)
                    .build();
            
            userRepository.save(testUser);
            log.info("Test user created successfully!");
            log.info("Username: testuser");
            log.info("Password: Test@123");
        } else {
            log.info("Users already exist. Skipping initialization.");
        }
    }
}
