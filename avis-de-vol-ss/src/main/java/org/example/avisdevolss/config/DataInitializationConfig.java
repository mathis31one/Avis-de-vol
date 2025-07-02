package org.example.avisdevolss.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Flight;
import org.example.avisdevolss.entity.Role;
import org.example.avisdevolss.service.AccountService;
import org.example.avisdevolss.service.FlightService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Calendar;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializationConfig {

    private final AccountService accountService;
    private final FlightService flightService;

    @Bean
    public CommandLineRunner initializeDefaultData() {
        return args -> {
            createDefaultAdminIfNotExists();
            createDefaultUserIfNotExists();
            createDefaultFlights();
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

    private void createDefaultUserIfNotExists() {
        String userEmail = "user@avisdevol.com";

        if (!accountService.existsByEmail(userEmail)) {
            log.info("Creating default user account...");
            try {
                Account userAccount = new Account();
                userAccount.setFirstName("John");
                userAccount.setLastName("Doe");
                userAccount.setEmail(userEmail);
                userAccount.setPassword("user123");
                userAccount.setRole(Role.USER);

                accountService.createAccount(userAccount);
                log.info("Default user account created successfully with email: {}", userEmail);
            } catch (Exception e) {
                log.error("Failed to create default user account", e);
            }
        } else {
            log.info("Default user account already exists");
        }
    }

    private void createDefaultFlights() {
        log.info("Creating default flights...");

        try {
            // Vol 1: Paris - New York
            createFlightIfNotExists("AF123", "Air France", createDate(2024, 8, 15));

            // Vol 2: Londres - Tokyo
            createFlightIfNotExists("BA456", "British Airways", createDate(2024, 9, 20));

            // Vol 3: Madrid - Los Angeles
            createFlightIfNotExists("IB789", "Iberia", createDate(2024, 10, 10));

            log.info("Default flights created successfully");
        } catch (Exception e) {
            log.error("Failed to create default flights", e);
        }
    }

    private void createFlightIfNotExists(String flightNumber, String company, Date date) {
        try {
            Flight flight = new Flight();
            flight.setFlightNumber(flightNumber);
            flight.setCompany(company);
            flight.setDate(date);

            flightService.createFlight(flight);
            log.info("Flight {} created successfully", flightNumber);
        } catch (IllegalArgumentException e) {
            log.info("Flight {} already exists", flightNumber);
        }
    }

    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // month is 0-based
        return calendar.getTime();
    }
}
