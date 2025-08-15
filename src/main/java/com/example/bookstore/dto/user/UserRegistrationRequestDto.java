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
    @NotBlank //inside @Password I already checking for the field being not blank, still need to
    // add @NotBlank though?
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
