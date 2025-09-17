package com.example.bookstore.service.book;

import static com.example.bookstore.testdata.BookTestData.createBookDtoSample;
import static com.example.bookstore.testdata.BookTestData.createBookDtoWithoutCategoryIds;
import static com.example.bookstore.testdata.BookTestData.createBookSample;
import static com.example.bookstore.testdata.BookTestData.createCreateBookRequestDtoSample;
import static com.example.bookstore.testdata.BookTestData.createUpdateBookRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.BookSearchParameters;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.book.PriceRange;
import com.example.bookstore.dto.book.UpdateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.book.BookSpecificationBuilder;
import java.math.BigDecimal;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("""
            Verify findById with the correct bookId returns book
            """)
    public void findById_withValidBookId_ShouldReturnValidBook() {
        // Given
        Book book = createBookSample();
        BookDto expectedDto = createBookDtoSample();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        // When
        BookDto actualDto = bookService.findById(book.getId());

        // Then
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("""
            Verify findById with invalid bookId throws an exception
            """)
    public void findById_withInvalidBookId_ShouldThrowException() {
        // Given
        Long invalidId = 99L;
        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(invalidId));
    }

    @Test
    @DisplayName("""
            Verify findAll returns a page of books with correct content
            """)
    public void findAll_WithValidPageable_ShouldReturnPageOfBookDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        Book book = createBookSample();
        List<Book> bookList = List.of(book);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        BookDto bookDto = createBookDtoSample();
        List<BookDto> bookDtoList = List.of(bookDto);
        Page<BookDto> expectedPage = new PageImpl<>(bookDtoList, pageable, bookDtoList.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        Page<BookDto> actualPage = bookService.findAll(pageable);

        // Then
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedPage.getContent().size(), actualPage.getContent().size());
        assertEquals(expectedPage.getContent(), actualPage.getContent());
    }

    @Test
    @DisplayName("""
            Verify save method creates a new book and returns a valid BookDto
            """)
    public void save_WithValidRequestDto_ShouldReturnValidBookDto() {
        // Given
        CreateBookRequestDto requestDto = createCreateBookRequestDtoSample();
        Book book = createBookSample();
        book.setId(null);

        Book savedBook = createBookSample();
        BookDto expectedDto = createBookDtoSample();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(expectedDto);

        // When
        BookDto actualDto = bookService.save(requestDto);

        // Then
        assertEquals(expectedDto, actualDto);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("""
            Verify deleteById method calls the repository delete method
            """)
    public void deleteById_ValidId_ShouldCallRepositoryDelete() {
        // Given
        Long bookId = 1L;

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("""
            Verify updateBook method updates the book with valid bookId
            """)
    public void updateBook_WithValidId_ShouldReturnValidBookDto() {
        // Given
        Long bookId = 1L;
        Book existingBook = createBookSample();
        BookDto expectedDto = createBookDtoSample();
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.toDto(existingBook)).thenReturn(expectedDto);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        // When
        BookDto actualDto = bookService.updateBook(bookId, requestDto);

        //Then
        assertEquals(expectedDto, actualDto);
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookMapper, times(1))
                .updateBookFromDto(requestDto, existingBook);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("""
            Verify updateBook with invalid bookId throws an exception
            """)
    public void updateBook_WithInvalidId_ShouldThrowException() {
        // Given
        Long invalidId = 90L;
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();
        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(invalidId, requestDto));
        verify(bookRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("""
            Verify search with valid parameters returns a page of BookDto
            """)
    public void search_WithValidSearchParameters_ShouldReturnPageBookDto() {
        // Given
        BookSearchParameters searchParameters = new BookSearchParameters(
                new String[]{"F. Scott Fitzgerald"},
                new PriceRange(BigDecimal.ZERO, BigDecimal.valueOf(50)),
                "The Great");
        Specification<Book> specification = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> bookList = List.of(createBookSample());
        Page<Book> expectedPage = new PageImpl<>(bookList, pageable, bookList.size());
        BookDto expectedDto = createBookDtoSample();

        when(bookSpecificationBuilder.build(any(BookSearchParameters.class))).thenReturn(
                specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(expectedPage);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDto);

        //When
        Page<BookDto> actualPage = bookService.search(searchParameters, pageable);

        //Then
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedPage.getContent().size(), actualPage.getContent().size());
        assertEquals(expectedDto, actualPage.getContent().get(0));

        verify(bookSpecificationBuilder, times(1)).build(any(BookSearchParameters.class));
        verify(bookRepository, times(1)).findAll(eq(specification), eq(pageable));
    }

    @Test
    @DisplayName("""
            Verify findBooksByCategoryId returns a page of books without category IDs
            """)
    public void findBooksByCategoryId_WithValidId_ShouldReturnValidBookDto() {
        // Given
        Long categoryId = 1L;
        List<Book> bookList = List.of(createBookSample());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());
        BookDtoWithoutCategoryIds expectedDto = createBookDtoWithoutCategoryIds();

        when(bookMapper.toDtoWithoutCategories(any(Book.class))).thenReturn(expectedDto);
        when(bookRepository.findAllByCategoriesId(categoryId, pageable)).thenReturn(bookPage);

        // When
        Page<BookDtoWithoutCategoryIds> actualPage = bookService.findBooksByCategoryId(
                categoryId, pageable);

        // Then
        assertEquals(bookPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(bookPage.getContent().size(), actualPage.getContent().size());
        assertEquals(expectedDto, actualPage.getContent().get(0));

        verify(bookRepository, times(1))
                .findAllByCategoriesId(categoryId, pageable);
        verify(bookMapper, times(1)).toDtoWithoutCategories(any(Book.class));
    }
}
