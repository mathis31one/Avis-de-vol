package org.example.avisdevolss.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.avisdevolss.entity.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
