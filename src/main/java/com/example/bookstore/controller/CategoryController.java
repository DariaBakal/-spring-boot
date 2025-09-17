package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.dto.category.UpdateCategoryRequestDto;
import com.example.bookstore.service.book.BookService;
import com.example.bookstore.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all categories",
            description = "Get a list of all available categories")
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get category by ID",
            description = "Get a category by its unique identifier")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get books by category",
            description = "Get a list of books by category id")
    public Page<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id,
            Pageable pageable) {
        return bookService.findBooksByCategoryId(id, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category",
            description = "Create a new category with the provided details")
    public CategoryDto createCategory(
            @RequestBody @Valid CreateCategoryRequestDto categoryRequestDto) {
        return categoryService.save(categoryRequestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing category",
            description = "Update the details of an existing category by its unique identifier")
    public CategoryDto updateCategory(@PathVariable Long id,
            @RequestBody @Valid UpdateCategoryRequestDto requestDto) {
        return categoryService.updateCategory(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category by ID",
            description = "Delete a category from the database by its unique identifier")
    public void delete(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
