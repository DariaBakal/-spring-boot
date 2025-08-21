package com.example.bookstore.dto.user;

import com.example.bookstore.validation.password.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank
    private String email;
    @Password
    private String password;
}
