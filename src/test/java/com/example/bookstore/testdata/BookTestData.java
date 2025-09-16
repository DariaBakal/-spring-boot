package com.example.bookstore.testdata;

import static com.example.bookstore.testdata.CategoryTestData.createCategorySample;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.book.UpdateBookRequestDto;
import com.example.bookstore.model.Book;
import java.math.BigDecimal;
import java.util.Set;

public class BookTestData {

    public static Book createBookSample() {
        return new Book()
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("12.99"))
                .setDescription("A classic novel about the American Dream.")
                .setCoverImage("https://example.com/gatsby.jpg")
                .setCategories(Set.of(createCategorySample()));
    }

    public static Book createAnotherBookSample() {
        return new Book()
                .setTitle("1984")
                .setAuthor("George Orwell")
                .setIsbn("978-0451524935")
                .setPrice(new BigDecimal("9.99"))
                .setDescription("A dystopian social science fiction novel and cautionary tale.")
                .setCoverImage("https://example.com/1984.jpg");
    }

    public static BookDto createBookDtoSample() {
        return new BookDto()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("12.99"))
                .setDescription("A classic novel about the American Dream.")
                .setCoverImage("https://example.com/gatsby.jpg")
                .setCategoryIds(Set.of(1L));

    }

    public static BookDto createDifferentBookDtoSample() {
        return new BookDto()
                .setId(2L)
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("978-0441172719")
                .setPrice(new BigDecimal("15.99"))
                .setDescription("A science fiction masterpiece set on the desert planet Arrakis.")
                .setCoverImage("https://example.com/dune.jpg")
                .setCategoryIds(Set.of(1L));
    }

    public static CreateBookRequestDto createCreateBookRequestDtoSample() {
        return new CreateBookRequestDto()
                .setTitle("Dune")
                .setAuthor("Frank Herbert")
                .setIsbn("978-0441172719")
                .setPrice(new BigDecimal("15.99"))
                .setDescription("A science fiction masterpiece set on the desert planet Arrakis.")
                .setCoverImage("https://example.com/dune.jpg")
                .setCategoryIds(Set.of(1L));
    }

    public static UpdateBookRequestDto createUpdateBookRequestDto() {
        return new UpdateBookRequestDto()
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("12.99"))
                .setDescription("A classic novel about the American Dream.")
                .setCoverImage("https://example.com/gatsby.jpg")
                .setCategoryIds(Set.of(1L));
    }

    public static UpdateBookRequestDto createUpdateBookRequestDtoWithoutAuthor() {
        return new UpdateBookRequestDto()
                .setTitle("The Great Gatsby")
                .setAuthor("")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("12.99"))
                .setDescription("A classic novel about the American Dream.")
                .setCoverImage("https://example.com/gatsby.jpg")
                .setCategoryIds(Set.of(1L));
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        return new BookDtoWithoutCategoryIds()
                .setId(1L)
                .setTitle("The Great Gatsby")
                .setAuthor("F. Scott Fitzgerald")
                .setIsbn("978-0743273565")
                .setPrice(new BigDecimal("12.99"))
                .setDescription("A classic novel about the American Dream.")
                .setCoverImage("https://example.com/gatsby.jpg");

    }
}
