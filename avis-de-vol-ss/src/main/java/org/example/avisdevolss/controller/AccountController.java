package org.example.avisdevolss.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.dto.AccountLoginDto;
import org.example.avisdevolss.dto.AccountRegistrationDto;
import org.example.avisdevolss.dto.AccountResponseDto;
import org.example.avisdevolss.dto.LoginResponseDto;
import org.example.avisdevolss.dto.PasswordUpdateDto;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.security.JwtTokenProvider;
import org.example.avisdevolss.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new account
     * @param registrationDto the registration data
     * @return the created account
     */
    @PostMapping("/register")
    public ResponseEntity<AccountResponseDto> registerAccount(@Valid @RequestBody AccountRegistrationDto registrationDto) {
        try {
            log.info("Registering new account for email: {}", registrationDto.getEmail());
            AccountResponseDto createdAccount = accountService.createAccount(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Login with email and password
     * @param loginDto the login credentials
     * @return JWT token and user info on success
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AccountLoginDto loginDto) {
        try {
            boolean isValid = accountService.verifyPassword(loginDto.getEmail(), loginDto.getPassword());
            if (isValid) {
                // Get user account
                Optional<Account> accountOpt = accountService.findByEmail(loginDto.getEmail());
                if (accountOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }
                
                Account account = accountOpt.get();
                
                // Generate JWT token
                String token = jwtTokenProvider.generateToken(account.getEmail(), account.getId());
                
                // Create response DTO without password
                AccountResponseDto userDto = new AccountResponseDto(
                    account.getId(),
                    account.getFirstName(),
                    account.getLastName(),
                    account.getEmail(),
                    account.getRole()
                );
                
                LoginResponseDto response = new LoginResponseDto(token, userDto);
                
                log.info("Successful login for email: {}", loginDto.getEmail());
                return ResponseEntity.ok(response);
            } else {
                log.warn("Failed login attempt for email: {}", loginDto.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    /**
     * Get account by ID
     * @param id the account ID
     * @return the account if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccount(@PathVariable Integer id) {
        Optional<AccountResponseDto> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get account by email
     * @param email the email address
     * @return the account if found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<AccountResponseDto> getAccountByEmail(@PathVariable String email) {
        Optional<AccountResponseDto> account = accountService.getAccountByEmail(email);
        return account.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all accounts
     * @return list of all accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        List<AccountResponseDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Search accounts by name
     * @param name the name to search for
     * @return list of matching accounts
     */
    @GetMapping("/search")
    public ResponseEntity<List<AccountResponseDto>> searchAccounts(@RequestParam String name) {
        List<AccountResponseDto> accounts = accountService.searchAccountsByName(name);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Update account password
     * @param id the account ID
     * @param passwordUpdateDto the new password data
     * @return success response
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Integer id, 
                                                @Valid @RequestBody PasswordUpdateDto passwordUpdateDto) {
        try {
            accountService.updatePassword(id, passwordUpdateDto);
            log.info("Password updated for account ID: {}", id);
            return ResponseEntity.ok("Password updated successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Password update failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete account
     * @param id the account ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Integer id) {
        try {
            accountService.deleteAccount(id);
            log.info("Account deleted with ID: {}", id);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Account deletion failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get account statistics
     * @return account count
     */
    @GetMapping("/stats")
    public ResponseEntity<Long> getAccountCount() {
        long count = accountService.getTotalAccountCount();
        return ResponseEntity.ok(count);
    }

    /**
     * Get current user's profile
     * @param request the HTTP request containing JWT token
     * @return current user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<AccountResponseDto> getCurrentUser(HttpServletRequest request) {
        try {
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Optional<AccountResponseDto> account = accountService.getAccountById(userId);
            return account.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
