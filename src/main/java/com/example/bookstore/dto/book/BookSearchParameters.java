package com.example.bookstore.dto.book;

public record BookSearchParameters(String[] authors, PriceRange range, String titlePart) {
}
