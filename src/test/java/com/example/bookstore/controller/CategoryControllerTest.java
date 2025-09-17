package com.example.bookstore.controller;

import static com.example.bookstore.testdata.BookTestData.createBookDtoWithoutCategoryIds;
import static com.example.bookstore.testdata.CategoryTestData.createAnotherCategoryDtoSample;
import static com.example.bookstore.testdata.CategoryTestData.createCategoryDtoSample;
import static com.example.bookstore.testdata.CategoryTestData.createCategoryRequestDtoSample;
import static com.example.bookstore.testdata.CategoryTestData.createCategoryWithoutBooks;
import static com.example.bookstore.testdata.CategoryTestData.createCreateCategoryRequestDtoSample;
import static com.example.bookstore.testdata.CategoryTestData.createUpdateCategoryRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.category.CategoryDto;
import com.example.bookstore.dto.category.CreateCategoryRequestDto;
import com.example.bookstore.dto.category.UpdateCategoryRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.category.CategoryRepository;
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
@Sql(scripts = "classpath:database/categories/category-controller-test-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

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
            Verify getAll method returns a Page of CategoryDtos
            """)
    public void getAll_WithValidRequest_ShouldReturnPageOfCategoryDtos()
            throws Exception {
        // Given
        List<CategoryDto> expectedCategories = List.of(createCategoryDtoSample());

        // When
        MvcResult result = mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<CategoryDto> actualCategories = objectMapper.readValue(
                root.get("content").toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class)
        );

        // Then
        assertEquals(1, actualCategories.size());
        assertEquals(expectedCategories, actualCategories);
        assertEquals(1, root.get("totalElements").asInt());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify getAll method for anonymous user return 401 Unauthorised""")
    public void getAll_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // When
        mockMvc.perform(
                        get("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getCategoryById method with valid categoryId returns CategoryDto
            """)
    public void getCategoryById_WithValidCategoryId_ShouldReturnCategoryDto() throws Exception {
        // Given
        CategoryDto expectedDto = createCategoryDtoSample();

        // When
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", expectedDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getCategoryById method with invalid categoryId returns 404 Not Found
            """)
    public void getCategoryById_WithInvalidCategoryId_ShouldReturnIsNotFound() throws Exception {
        // Given
        Long invalidId = 90L;

        // When
        mockMvc.perform(
                        get("/categories/{id}", invalidId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify getCategoryById method for anonymous user returns 401 Unauthorised
            """)
    public void getCategoryById_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        CategoryDto categoryDto = createCategoryDtoSample();

        // When
        mockMvc.perform(
                        get("/categories/{id}", categoryDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getBooksByCategoryId method with valid categoryId returns Page of
            BookDtoWithoutCategoryIds
            """)
    public void getBooksByCategoryId_WithValidCategoryId_ShouldReturnPageOfBookDtos()
            throws Exception {
        // Given
        Long validId = 1L;
        List<BookDtoWithoutCategoryIds> expectedBooks = List.of(createBookDtoWithoutCategoryIds());

        // When
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}/books", validId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDtoWithoutCategoryIds> actualBooks = objectMapper.readValue(
                root.get("content").toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDtoWithoutCategoryIds.class));

        // Then
        assertEquals(expectedBooks, actualBooks);
        assertEquals(1, root.get("totalElements").asInt());
        assertEquals(1, root.get("totalPages").asInt());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify getBooksByCategoryId method with categoryId without books returns empty page
            """)
    public void getBooksByCategoryId_WithCategoryIdWithoutBooks_ShouldReturnEmptyPage()
            throws Exception {
        // Given
        Category categoryWithoutBooks = categoryRepository.save(createCategoryWithoutBooks());

        // When
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}/books", categoryWithoutBooks.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        List<BookDtoWithoutCategoryIds> actualBooks = objectMapper.readValue(
                root.get("content").toString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, BookDtoWithoutCategoryIds.class));

        // Then
        assertTrue(actualBooks.isEmpty());
        assertEquals(0, root.get("totalElements").asInt());
        assertEquals(0, root.get("totalPages").asInt());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify getBooksByCategoryId with anonymous user returns 401 Unauthorised
            """)
    public void getBooksByCategoryId_WithAnonymousUser_ShouldReturnIsUnauthorised()
            throws Exception {
        // Given
        Long validId = 1L;

        // When
        mockMvc.perform(
                        get("/categories/{id}/books", validId)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify createCategory with valid request dto returns CategoryDto
            """)
    public void createCategory_WithValidRequestDto_ShouldReturnCategoryDto() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = createCreateCategoryRequestDtoSample();
        CategoryDto expectedDto = createAnotherCategoryDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        // Then
        assertNotNull(actualDto);
        assertNotNull(actualDto.getId());
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify createCategory with invalid request dto returns 400 Bad Request
            """)
    public void createCategory_WithInvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        requestDto.setName(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify createCategory with incorrect role returns 403 Forbidden
            """)
    public void createCategory_WithIncorrectRole_ShouldReturnIsForbidden() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify createCategory with unauthorised user returns 401 Unauthorized
            """)
    public void createCategory_WithUnauthorisedUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        CreateCategoryRequestDto requestDto = createCategoryRequestDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateCategory with correct CategoryId returns CategoryDto
            """)
    public void updateCategory_WithCorrectCategoryId_ShouldReturnCategoryDto() throws Exception {
        // Given
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        CategoryDto expectedDto = createCategoryDtoSample();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", expectedDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actualDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        // Then
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateCategory with invalid CategoryId returns 404 Not Found
            """)
    public void updateCategory_WithInvalidCategoryId_ShouldReturnNotFound() throws Exception {
        // Given
        Long invalidId = 90L;
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/categories/{id}", invalidId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify updateCategory with invalid request dto returns 400 Bad Request
            """)
    public void updateCategory_WithInvalidRequestDto_ShouldReturnBadRequest() throws Exception {
        // Given
        CategoryDto bookDto = createCategoryDtoSample();
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        requestDto.setName(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/categories/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Verify updateCategory with incorrect role returns 403 Forbidden
            """)
    public void updateCategory_WithInCorrectRole_ShouldReturnIsForbidden() throws Exception {
        // Given
        CategoryDto bookDto = createCategoryDtoSample();
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/categories/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    @DisplayName("""
            Verify updateCategory with anonymous user returns 401 Unauthorised
            """)
    public void updateCategory_WithAnonymousUser_ShouldReturnIsUnauthorised() throws Exception {
        // Given
        CategoryDto bookDto = createCategoryDtoSample();
        UpdateCategoryRequestDto requestDto = createUpdateCategoryRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        mockMvc.perform(
                        put("/categories/{id}", bookDto.getId())
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify delete with correct CategoryId returns 204 No Content
            """)
    public void delete_WithCorrectCategoryId_ShouldReturnNoContent() throws Exception {
        // Given
        Category category = categoryRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with id 1"));

        // When
        mockMvc.perform(
                        delete("/categories/{id}", category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Verify delete with invalid CategoryId returns 204 No Content
            """)
    public void delete_WithInvalidCategoryId_ShouldReturnNoContent() throws Exception {
        // Given
        Long invalidId = 90L;
        // When
        mockMvc.perform(
                        delete("/categories/{id}", invalidId)
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
        Category category = categoryRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with id 1"));
        // When
        mockMvc.perform(
                        delete("/categories/{id}", category.getId())
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
        Category category = categoryRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Can't find category with id 1"));
        // When
        mockMvc.perform(
                        delete("/categories/{id}", category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // Then
                .andExpect(status().isUnauthorized());
    }
}
