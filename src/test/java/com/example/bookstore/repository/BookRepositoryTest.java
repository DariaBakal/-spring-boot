package com.example.bookstore.repository;

import static com.example.bookstore.testdata.BookTestData.createAnotherBookSample;
import static com.example.bookstore.testdata.BookTestData.createBookSample;
import static com.example.bookstore.testdata.CategoryTestData.createCategorySample;
import static com.example.bookstore.testdata.CategoryTestData.createCategoryWithoutBooks;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.category.CategoryRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanDb() {
        bookRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("""
            Verify findAllByCategoriesId with correct categoryId returns page of books
            """)
    public void findAllByCategoriesId_WithCorrectCategoryId_ShouldReturnPageOfBooks() {
        //Given
        Category category = categoryRepository.save(createCategorySample());

        Book savedBook1 = bookRepository.save(createBookSample());
        savedBook1.setCategories(Set.of(category));
        Book savedBook2 = bookRepository.save(createAnotherBookSample());
        savedBook2.setCategories(Set.of(category));
        List<Book> bookList = List.of(savedBook1, savedBook2);
        Pageable pageable = PageRequest.of(0, 20);

        Page<Book> expectedPage = new PageImpl<>(bookList, pageable, bookList.size());

        // When
        Page<Book> actualPage = bookRepository.findAllByCategoriesId(category.getId(),
                pageable);

        //Then
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedPage.getContent().size(), actualPage.getContent().size());
        assertEquals(expectedPage.getContent(), actualPage.getContent());
    }

    @Test
    @DisplayName("""
            Verify findAllByCategoriesId with category with no existing books returns empty page
            and doesn't cause an error""")
    public void findAllByCategoriesId_WithUnusableCategoryId_ShouldReturnEmptyPage() {
        // Given
        Category categoryWithoutBooks = categoryRepository.save(createCategoryWithoutBooks());
        Pageable pageable = PageRequest.of(0, 20);

        //When
        Page<Book> actualPage = bookRepository.findAllByCategoriesId(categoryWithoutBooks.getId(),
                pageable);

        //Then
        assertTrue(actualPage.isEmpty());
    }
}
