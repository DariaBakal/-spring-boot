package com.example.bookstore.dto.user;

import com.example.bookstore.validation.FieldMatch;
import com.example.bookstore.validation.password.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@FieldMatch(field = "password", fieldMatch = "repeatPassword", message = "Passwords do not match!")
public class UserRegistrationRequestDto {
    @NotBlank
    private String email;
    @Password
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
