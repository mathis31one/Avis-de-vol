package org.example.avisdevolss.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializationConfig {

    private final AccountService accountService;

    @Bean
    public CommandLineRunner initializeDefaultData() {
        return args -> {
            createDefaultAdminIfNotExists();
        };
    }

    private void createDefaultAdminIfNotExists() {
        String adminEmail = "admin@avisdevol.com";
        
        if (!accountService.existsByEmail(adminEmail)) {
            log.info("Creating default admin account...");
            try {
                accountService.createAdminAccount(
                    "Admin",
                    "User",
                    adminEmail,
                    "admin123"
                );
                log.info("Default admin account created successfully with email: {}", adminEmail);
            } catch (Exception e) {
                log.error("Failed to create default admin account", e);
            }
        } else {
            log.info("Default admin account already exists");
        }
    }
}
