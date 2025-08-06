package com.example.bookstore.dto;

import java.math.BigDecimal;

public record PriceRange(BigDecimal priceFrom, BigDecimal priceTo) {
}
