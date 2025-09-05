package com.example.bookstore.mapper;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.service.category.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookMapperHelper {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;

    @Named("toCategoryDto")
    public Set<Category> toCategoryDto(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryService::findById)
                .map(categoryMapper::toEntity)
                .collect(Collectors.toSet());
    }

    @Named("bookFromId")
    public Book bookFromId(Long id) {
        return bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id: " + id)
        );
    }
}
