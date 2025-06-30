package org.example.avisdevolss.repository;

import org.example.avisdevolss.entity.Account;
import org.example.avisdevolss.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    
    Optional<Account> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<Account> findByFirstNameContainingIgnoreCase(String firstName);
    
    List<Account> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Account> findByRole(Role role);
    
    @Query("SELECT a FROM Account a WHERE a.firstName LIKE %:name% OR a.lastName LIKE %:name%")
    List<Account> findByFullNameContaining(@Param("name") String name);
}
