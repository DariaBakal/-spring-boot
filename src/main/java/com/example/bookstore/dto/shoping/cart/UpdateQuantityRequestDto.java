package com.example.bookstore.dto.shoping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateQuantityRequestDto {
    @NotNull
    @Positive
    private int quantity;
}
