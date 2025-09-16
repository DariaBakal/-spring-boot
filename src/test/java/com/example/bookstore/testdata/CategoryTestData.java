package com.example.bookstore.testdata;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.dto.category.UpdateCategoryRequestDto;
import com.example.bookstore.model.Category;

public class CategoryTestData {
    public static Category createCategorySample() {
        return new Category()
                .setName("Fiction")
                .setDescription("Fiction books");
    }

    public static Category createCategoryWithoutBooks() {
        return new Category()
                .setName("Category without books")
                .setDescription("Category to check whether method returns empty page.");
    }

    public static CategoryDto createCategoryDtoSample() {
        return new CategoryDto()
                .setId(1L)
                .setName("Fiction")
                .setDescription("A genre of imaginative, non-factual storytelling.");
    }

    public static CategoryDto createAnotherCategoryDtoSample() {
        return new CategoryDto()
                .setId(2L)
                .setName("Science")
                .setDescription("Books that explore scientific concepts and discoveries.");
    }

    public static CreateCategoryRequestDto createCategoryRequestDtoSample() {
        return new CreateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("A genre of imaginative, non-factual storytelling.");
    }

    public static UpdateCategoryRequestDto createUpdateCategoryRequestDto() {
        return new UpdateCategoryRequestDto()
                .setName("Fiction")
                .setDescription("A genre of imaginative, non-factual storytelling.");
    }

    public static CreateCategoryRequestDto createCreateCategoryRequestDtoSample() {
        return new CreateCategoryRequestDto()
                .setName("Science")
                .setDescription("Books that explore scientific concepts and discoveries.");
    }
}
