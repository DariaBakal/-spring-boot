package com.example.bookstore.dto.order;

import com.example.bookstore.model.Order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    @NotNull
    private OrderStatus status;
}
