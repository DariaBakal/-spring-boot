package com.example.bookstore.dto;

import com.example.bookstore.validation.image.Image;
import com.example.bookstore.validation.isbn.Isbn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class UpdateBookRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    @Isbn
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    @NotNull
    private String description;
    @NotNull
    @Image
    private String coverImage;
}
