package com.example.bookstore.dto.book;

import java.math.BigDecimal;

public record PriceRange(BigDecimal priceFrom, BigDecimal priceTo) {
}
