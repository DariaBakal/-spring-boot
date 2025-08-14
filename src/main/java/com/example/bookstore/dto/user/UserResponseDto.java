package com.example.bookstore.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserResponseDto {
    @NotNull
    private Long id;
    @NotBlank
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
