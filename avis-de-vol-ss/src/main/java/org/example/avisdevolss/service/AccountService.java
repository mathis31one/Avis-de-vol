package org.example.avisdevolss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.avisdevolss.dto.AccountRegistrationDto;
import org.example.avisdevolss.dto.AccountResponseDto;
import org.example.avisdevolss.dto.PasswordUpdateDto;
import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Role;
import org.example.avisdevolss.repository.AccountRepository;
import org.example.avisdevolss.repository.ReviewRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new account
     * @param account the account to create
     * @return the created account
     * @throws IllegalArgumentException if email already exists
     */
    public Account createAccount(Account account) {
        log.info("Creating new account for email: {}", account.getEmail());
        
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new IllegalArgumentException("Account with email " + account.getEmail() + " already exists");
        }
        
        // Encrypt password before saving
        if (account.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        
        Account savedAccount = accountRepository.save(account);
        log.info("Successfully created account with ID: {}", savedAccount.getId());
        return savedAccount;
    }

    /**
     * Find account by ID
     * @param id the account ID
     * @return Optional containing the account if found
     */
    @Transactional(readOnly = true)
    public Optional<Account> findById(Integer id) {
        log.debug("Finding account by ID: {}", id);
        return accountRepository.findById(id);
    }

    /**
     * Find account by email
     * @param email the email address
     * @return Optional containing the account if found
     */
    @Transactional(readOnly = true)
    public Optional<Account> findByEmail(String email) {
        log.debug("Finding account by email: {}", email);
        return accountRepository.findByEmail(email);
    }

    /**
     * Get all accounts
     * @return list of all accounts
     */
    @Transactional(readOnly = true)
    public List<Account> findAllAccounts() {
        log.debug("Retrieving all accounts");
        return accountRepository.findAll();
    }

    /**
     * Update an existing account
     * @param id the account ID
     * @param updatedAccount the updated account information
     * @return the updated account
     * @throws IllegalArgumentException if account not found or email conflict
     */
    public Account updateAccount(Integer id, Account updatedAccount) {
        log.info("Updating account with ID: {}", id);
        
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));

        // Check if email is being changed and if new email already exists
        if (!existingAccount.getEmail().equals(updatedAccount.getEmail()) && 
            accountRepository.existsByEmail(updatedAccount.getEmail())) {
            throw new IllegalArgumentException("Account with email " + updatedAccount.getEmail() + " already exists");
        }

        // Update fields
        existingAccount.setFirstName(updatedAccount.getFirstName());
        existingAccount.setLastName(updatedAccount.getLastName());
        existingAccount.setEmail(updatedAccount.getEmail());
        
        // Only update password if provided
        if (updatedAccount.getPassword() != null && !updatedAccount.getPassword().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(updatedAccount.getPassword()));
        }

        Account savedAccount = accountRepository.save(existingAccount);
        log.info("Successfully updated account with ID: {}", savedAccount.getId());
        return savedAccount;
    }

    /**
     * Delete account by ID
     * @param id the account ID
     * @throws IllegalArgumentException if account not found or has associated reviews
     */
    public void deleteAccount(Integer id) {
        log.info("Deleting account with ID: {}", id);
        
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        
        // Check if account has associated reviews
        if (reviewRepository.existsByAccountId(id)) {
            throw new IllegalArgumentException("Cannot delete account with ID: " + id + " because it has associated reviews");
        }

        accountRepository.deleteById(id);
        log.info("Successfully deleted account with ID: {}", id);
    }

    /**
     * Check if account exists by email
     * @param email the email address
     * @return true if account exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    /**
     * Search accounts by first name
     * @param firstName the first name to search for
     * @return list of matching accounts
     */
    @Transactional(readOnly = true)
    public List<Account> findByFirstName(String firstName) {
        log.debug("Searching accounts by first name: {}", firstName);
        return accountRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    /**
     * Search accounts by last name
     * @param lastName the last name to search for
     * @return list of matching accounts
     */
    @Transactional(readOnly = true)
    public List<Account> findByLastName(String lastName) {
        log.debug("Searching accounts by last name: {}", lastName);
        return accountRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    /**
     * Search accounts by full name (first or last name)
     * @param name the name to search for
     * @return list of matching accounts
     */
    @Transactional(readOnly = true)
    public List<Account> findByFullName(String name) {
        log.debug("Searching accounts by full name: {}", name);
        return accountRepository.findByFullNameContaining(name);
    }

    /**
     * Update password for an account
     * @param id the account ID
     * @param newPassword the new password
     * @throws IllegalArgumentException if account not found
     */
    public void updatePassword(Integer id, String newPassword) {
        log.info("Updating password for account ID: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        log.info("Successfully updated password for account ID: {}", id);
    }

    /**
     * Verify account password
     * @param email the email address
     * @param rawPassword the raw password to verify
     * @return true if password matches
     */
    @Transactional(readOnly = true)
    public boolean verifyPassword(String email, String rawPassword) {
        log.debug("Verifying password for email: {}", email);
        
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            return false;
        }
        
        return passwordEncoder.matches(rawPassword, accountOpt.get().getPassword());
    }

    /**
     * Get total count of accounts
     * @return total number of accounts
     */
    @Transactional(readOnly = true)
    public long getTotalAccountCount() {
        return accountRepository.count();
    }

    // DTO-based methods

    /**
     * Create account from registration DTO
     * @param registrationDto the registration data
     * @return account response DTO
     */
    public AccountResponseDto createAccount(AccountRegistrationDto registrationDto) {
        Account account = new Account();
        account.setFirstName(registrationDto.getFirstName());
        account.setLastName(registrationDto.getLastName());
        account.setEmail(registrationDto.getEmail());
        account.setPassword(registrationDto.getPassword());
        account.setRole(Role.USER); // Set default role to USER
        
        Account savedAccount = createAccount(account);
        return convertToResponseDto(savedAccount);
    }

    /**
     * Create admin account
     * @param firstName admin first name
     * @param lastName admin last name
     * @param email admin email
     * @param password admin password
     * @return account response DTO
     */
    public AccountResponseDto createAdminAccount(String firstName, String lastName, String email, String password) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setEmail(email);
        account.setPassword(password);
        account.setRole(Role.ADMIN);
        
        Account savedAccount = createAccount(account);
        return convertToResponseDto(savedAccount);
    }

    /**
     * Update account role
     * @param id the account ID
     * @param role the new role
     * @return updated account response DTO
     */
    public AccountResponseDto updateAccountRole(Integer id, Role role) {
        log.info("Updating role for account ID: {} to {}", id, role);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        
        account.setRole(role);
        Account savedAccount = accountRepository.save(account);
        log.info("Successfully updated role for account ID: {}", id);
        return convertToResponseDto(savedAccount);
    }

    /**
     * Get all admin accounts
     * @return list of admin account response DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAdminAccounts() {
        return accountRepository.findByRole(Role.ADMIN).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all user accounts
     * @return list of user account response DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllUserAccounts() {
        return accountRepository.findByRole(Role.USER).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if account is admin
     * @param id the account ID
     * @return true if account is admin
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(Integer id) {
        return findById(id)
                .map(account -> account.getRole() == Role.ADMIN)
                .orElse(false);
    }

    /**
     * Get account by ID as DTO
     * @param id the account ID
     * @return Optional containing account response DTO
     */
    @Transactional(readOnly = true)
    public Optional<AccountResponseDto> getAccountById(Integer id) {
        return findById(id).map(this::convertToResponseDto);
    }

    /**
     * Get account by email as DTO
     * @param email the email address
     * @return Optional containing account response DTO
     */
    @Transactional(readOnly = true)
    public Optional<AccountResponseDto> getAccountByEmail(String email) {
        return findByEmail(email).map(this::convertToResponseDto);
    }

    /**
     * Get all accounts as DTOs
     * @return list of account response DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts() {
        return findAllAccounts().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update password using DTO
     * @param id the account ID
     * @param passwordUpdateDto the password update data
     */
    public void updatePassword(Integer id, PasswordUpdateDto passwordUpdateDto) {
        updatePassword(id, passwordUpdateDto.getNewPassword());
    }

    /**
     * Search accounts by name and return as DTOs
     * @param name the name to search for
     * @return list of matching account response DTOs
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDto> searchAccountsByName(String name) {
        return findByFullName(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Utility methods

    /**
     * Convert Account entity to AccountResponseDto
     * @param account the account entity
     * @return account response DTO
     */
    private AccountResponseDto convertToResponseDto(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getFirstName(),
                account.getLastName(),
                account.getEmail(),
                account.getRole()
        );
    }
}
