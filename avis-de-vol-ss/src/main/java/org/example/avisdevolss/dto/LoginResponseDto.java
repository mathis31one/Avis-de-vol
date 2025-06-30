package org.example.avisdevolss.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String type = "Bearer";
    private AccountResponseDto user;

    public LoginResponseDto(String token, AccountResponseDto user) {
        this.token = token;
        this.user = user;
    }
}
