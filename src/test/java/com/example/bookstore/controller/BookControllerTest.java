package com.example.bookstore.controller;

import static com.example.bookstore.testdata.BookTestData.createBookDtoSample;
import static com.example.bookstore.testdata.BookTestData.createCreateBookRequestDtoSample;
import static com.example.bookstore.testdata.BookTestData.createDifferentBookDtoSample;
import static com.example.bookstore.testdata.BookTestData.createUpdateBookRequestDto;
import static com.example.bookstore.testdata.BookTestData.createUpdateBookRequestDtoWithoutAuthor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.dto.book.UpdateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.book.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/books/book-controller-test-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    static void beforeAll(WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getAll method returns a Page of BookDtos
            """)
    public void getAll_WithValidRequest_ShouldReturnPageOfBookDtos()
            throws Exception {
        // Given
        List<BookDto> expectedBook = List.of(createBookDtoSample());

        // When
        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDto> actualBooks = objectMapper.readValue(
                root.get("content").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class)
        );
        // Then
        assertEquals(1, actualBooks.size());
        assertEquals(expectedBook, actualBooks);
        assertEquals(1, root.get("totalElements").asInt());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify getAll method for anonymous user return 401 Unauthorised""")
    public void getAll_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // When
        mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getBookById method with valid bookId returns BookDto
            """)
    public void getBookById_WithValidBookId_ShouldReturnBookDto() throws Exception {
        // Given
        BookDto expectedDto = createBookDtoSample();

        //When
        MvcResult result = mockMvc.perform(
                        get("/books/{id}", expectedDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getBookById method with invalid bookId returns 404 Not Found
            """)
    public void getBookById_WithInvalidBookId_ShouldReturnIsNotFound() throws Exception {
        // Given
        Long invalidId = 90L;

        // When
        mockMvc.perform(
                        get("/books/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify getBookById method for anonymous user returns 401 Unauthorised
            """)
    public void getBookById_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        BookDto bookDto = createBookDtoSample();

        // When
        mockMvc.perform(
                        get("/books/{id}", bookDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify createBook with valid request dto returns BookDto""")
    public void createBook_WithValidRequestDto_ShouldReturnBookDto() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createCreateBookRequestDtoSample();
        BookDto expectedDto = createDifferentBookDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        // Then
        assertNotNull(actualDto);
        assertNotNull(actualDto.getId());
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify createBook with invalid request dto returns 400 Bad Request
            """)
    public void createBook_WithInvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createCreateBookRequestDtoSample();
        requestDto.setAuthor(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify createBook with incorrect role returns 403 Forbidden
            """)
    public void createBook_WithIncorrectRole_ShouldReturnIsForbidden() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createCreateBookRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify createBook with unauthorised user returns 401 Unauthorized
            """)
    public void createBook_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        CreateBookRequestDto requestDto = createCreateBookRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify delete with correct BookId returns 204 No Content
            """)
    public void delete_WithCorrectBookId_ShouldReturnNoContent() throws Exception {
        // Given
        Book book = bookRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id 1"));

        // When
        mockMvc.perform(
                        delete("/books/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify delete with invalid BookId returns 204 No Content
            """)
    public void delete_WithInvalidBookId_ShouldReturnNoContent() throws Exception {
        // Given
        Long invalidId = 90L;
        // When
        mockMvc.perform(
                        delete("/books/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify delete with incorrect role returns 403 Forbidden
            """)
    public void delete_WithIncorrectRole_ShouldReturnIsForbidden() throws Exception {
        // Given
        Book book = bookRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id 1"));
        // When
        mockMvc.perform(
                        delete("/books/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify delete for anonymous user returns 401 Unauthorised
            """)
    public void delete_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        Book book = bookRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find book with id 1"));
        // When
        mockMvc.perform(
                        delete("/books/{id}", book.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateBook with correct BookId returns BookDto
            """)
    public void updateBook_WithCorrectBookId_ShouldReturnBookDto() throws Exception {
        // Given
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();
        BookDto expectedDto = createBookDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/books/{id}", expectedDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateBook with invalid BookId returns 404 Not Found
            """)
    public void updateBook_WithInvalidBookId_ShouldReturnNotFound() throws Exception {
        // Given
        Long invalidId = 90L;
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/books/{id}", invalidId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateBook with invalid request dto returns 400 Bad Request
            """)
    public void updateBook_WithInvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        // Given
        BookDto bookDto = createBookDtoSample();
        UpdateBookRequestDto requestDto = createUpdateBookRequestDtoWithoutAuthor();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/books/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify updateBook with incorrect role returns 403 Forbidden
            """)
    public void updateBook_WithInCorrectRole_ShouldReturnIsForbidden() throws Exception {
        // Given
        BookDto bookDto = createBookDtoSample();
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/books/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify updateBook with anonymous user returns 401 Unauthorised
            """)
    public void updateBook_WithAnonymousUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        BookDto bookDto = createBookDtoSample();
        UpdateBookRequestDto requestDto = createUpdateBookRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/books/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify searchBooks with valid parameters returns a Page of BookDto
            """)
    public void searchBooks_WithValidParameters_ShouldReturnPageOfBookDto() throws Exception {
        // Given
        String queryParamTitle = "The Great Gatsby";
        List<BookDto> expectedBooks = List.of(createBookDtoSample());

        // When
        MvcResult result = mockMvc.perform(
                        get("/books/search")
                                .param("titlePart", queryParamTitle)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDto> actualBooks = objectMapper.readValue(root.get("content").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));

        // Then
        assertEquals(expectedBooks, actualBooks);
        assertEquals(1, root.get("totalElements").asInt());
        assertEquals(1, root.get("totalPages").asInt());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify searchBooks with no matching book returns empty page
            """)
    public void searchBooks_WithNoMatchingBook_ShouldReturnEmptyPage() throws Exception {
        // Given
        String queryParamTitle = "Non-existent Title";

        // When
        MvcResult result = mockMvc.perform(
                        get("/books/search")
                                .param("titlePart", queryParamTitle)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDto> actualBooks = objectMapper.readValue(root.get("content").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
        // Then
        assertTrue(actualBooks.isEmpty());
        assertEquals(0, root.get("totalElements").asInt());
        assertEquals(0, root.get("totalPages").asInt());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify searchBooks with anonymous user returns 401 Unauthorised
            """)
    public void searchBooks_WithAnonymousUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        String queryParamTitle = "The Great Gatsby";

        // When
        mockMvc.perform(
                        get("/books/search")
                                .param("titlePart", queryParamTitle)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isUnauthorized());
    }
}
