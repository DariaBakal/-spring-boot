package com.example.bookstore.dto;

public record BookSearchParameters(String[] authors, PriceRange range, String titlePart) {
}
