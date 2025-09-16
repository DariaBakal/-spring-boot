package com.example.bookstore.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.dto.category.UpdateCategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Category createCategorySample() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("A genre of imaginative, non-factual storytelling.");
        return category;
    }

    public static CategoryDto createCategoryDtoSample() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Fiction");
        categoryDto.setDescription("A genre of imaginative, non-factual storytelling.");
        return categoryDto;
    }

    private static CreateCategoryRequestDto createCategoryRequestDtoSample() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("A genre of imaginative, non-factual storytelling.");
        return requestDto;
    }

    private static UpdateCategoryRequestDto createUpdateCategoryRequestDto() {
        UpdateCategoryRequestDto requestDto = new UpdateCategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("A genre of imaginative, non-factual storytelling(updated).");
        return requestDto;
    }

    @Test
    @DisplayName("""
            Verify findAll with valid pageable returns a page of categories
            """)
    public void findAll_WithValidPageable_ShouldReturnPageOfCategoryDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        Category category = createCategorySample();
        List<Category> categoryList = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categoryList, pageable, categoryList.size());

        CategoryDto categoryDto = createCategoryDtoSample();
        List<CategoryDto> categoryDtoList = List.of(categoryDto);
        Page<CategoryDto> categoryDtoPage = new PageImpl<>(categoryDtoList, pageable,
                categoryDtoList.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        // When
        Page<CategoryDto> actualPage = categoryService.findAll(pageable);

        // Then
        assertEquals(categoryDtoPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(categoryDtoPage.getContent().size(), actualPage.getContent().size());
        assertEquals(categoryDtoPage.getContent(), actualPage.getContent());
    }

    @Test
    @DisplayName("""
            Verify findById with the correct categoryId returns category
            
            """)
    public void findById_WithValidCategoryId_ShouldReturnValidCategory() {
        // Given
        Category category = createCategorySample();
        Long validId = category.getId();
        CategoryDto categoryDto = createCategoryDtoSample();

        when(categoryRepository.findById(validId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        //When
        CategoryDto actualDto = categoryService.findById(validId);

        // Then
        assertEquals(categoryDto, actualDto);
        verify(categoryRepository, times(1)).findById(validId);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("""
            Verify findById with invalid categoryId throws Exception
            
            """)
    public void findById_WithInvalidCategoryId_ShouldThrowException() {
        // Given
        Long invalidId = 90L;
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(invalidId));

    }

    @Test
    @DisplayName("""
            Verify save method creates a new category and returns a valid CategoryDto
            """)
    public void save_WithValidRequestDto_ShouldReturnValidCategoryDto() {
        // Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        Category category = createCategorySample();
        category.setId(null);

        Category savedCategory = createCategorySample();
        CategoryDto expectedDto = createCategoryDtoSample();

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDto);

        // When
        CategoryDto actualDto = categoryService.save(requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("""
            Verify deleteById method calls the repository delete method
            """)
    public void deleteById_ValidId_ShouldCallRepositoryDelete() {
        // Given
        Long validId = 1L;

        // When
        categoryService.deleteById(validId);

        // Then
        verify(categoryRepository, times(1)).deleteById(validId);
    }

    @Test
    @DisplayName("""
            Verify updateCategory method updates the category with valid categoryId
            """)
    public void updateCategory_WithValidId_ShouldReturnValidCategoryDto() {
        // Given
        Long categoryId = 1L;
        Category existingCategory = createCategorySample();
        CategoryDto expectedDto = createCategoryDtoSample();
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toDto(existingCategory)).thenReturn(expectedDto);
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        // When
        CategoryDto actualDto = categoryService.updateCategory(categoryId, requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(existingCategory);
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    @DisplayName("""
            Verify updateCategory method with invalid id throws an exception
            """)
    public void updateCategory_WithInvalidId_ShouldThrowException() {
        // Given
        Long invalidId = 90L;
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateCategory(invalidId, requestDto));
        verify(categoryRepository, times(1)).findById(invalidId);

    }
}
