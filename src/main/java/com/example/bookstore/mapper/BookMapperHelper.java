package com.example.bookstore.mapper;

import com.example.bookstore.model.Category;
import com.example.bookstore.service.category.CategoryService;
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

    @Named("toCategoryDto")
    public Set<Category> toCategoryDto(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryService::findById)
                .map(categoryMapper::toEntity)
                .collect(Collectors.toSet());
    }
}
