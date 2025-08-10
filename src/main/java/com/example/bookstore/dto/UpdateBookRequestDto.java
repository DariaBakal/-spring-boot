package com.example.bookstore.dto;

import com.example.bookstore.validation.image.Image;
import com.example.bookstore.validation.isbn.Isbn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class UpdateBookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @Isbn
    private String isbn;
    @NotNull
    @Positive
    private BigDecimal price;
    private String description;
    @Image
    private String coverImage;
}
