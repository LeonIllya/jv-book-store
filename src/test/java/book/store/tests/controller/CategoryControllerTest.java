package book.store.tests.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource("database/books/add-books.sql"));
        }
    }

    @AfterEach
    void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/books/remove-books.sql"));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new category")
    void createCategory_validRequestDto_success() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto categoryDtoExpected = toCategoryDto(categoryRequestDto);
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto categoryDtoActual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(categoryDtoActual);
        EqualsBuilder.reflectionEquals(categoryDtoExpected, categoryDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update category")
    void updateCategory_validRequestDto_success() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Long id = 1L;
        CategoryDto categoryDtoExpected = toCategoryDto(categoryRequestDto);
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/categories/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        CategoryDto categoryDtoActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(categoryDtoActual);
        EqualsBuilder.reflectionEquals(categoryDtoExpected, categoryDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Can`t update category by id")
    void updateCategory_invalidRequestDto_returnEntityNotFoundException() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Long id = 100L;
        String expected = "Can`t find a category by id: " + id;
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/categories/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String message = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        Assertions.assertEquals(expected, message);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete category by id")
    void deleteCategory_validRequestDto_success() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        long id = 1;
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        mockMvc.perform(delete("/categories/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get category by id from database")
    void getCategoryById_validRequestDto_returnCategoryDto() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Long id = 2L;
        CategoryDto categoryDtoExpected = toCategoryDto(categoryRequestDto);
        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(get("/categories/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto categoryDtoActual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(categoryDtoActual);
        EqualsBuilder.reflectionEquals(categoryDtoExpected, categoryDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Can`t get category by id from database")
    void getCategoryById_validRequestDto_returnEntityNotFoundExcdption() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Long id = 100L;
        String expected = "Can`t find a category by id: " + id;

        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(get("/categories/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = Objects.requireNonNull(result.getResolvedException()).getMessage();
        Assertions.assertEquals(expected, message);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get all categories")
    void getAllCategories_validCategories_returnListOfCategories() throws Exception {
        List<CategoryDto> listOfCategoriesDtoExpected = createListOfCategoriesDto();

        MvcResult mvcResult = mockMvc.perform(get("/categories")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] categoryDtoListActual = objectMapper.readValue(mvcResult.getResponse()
            .getContentAsString(), CategoryDto[].class);
        Assertions.assertEquals(2L, categoryDtoListActual.length);
        Assertions.assertEquals(listOfCategoriesDtoExpected,
                Arrays.stream(categoryDtoListActual).toList());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get books by category id")
    void getBooksByCategory_validCategories_returnListOfBooks() throws Exception {
        List<BookDtoWithoutCategoryIds> listOfBooksDtoExpected =
                createListOfBooksDtoWithoutCategoryId();
        long categoryId = 2;

        MvcResult mvcResult = mockMvc.perform(get("/categories/{id}/books", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] bookDtoWithoutCategoryIdsActual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), BookDtoWithoutCategoryIds[].class);
        Assertions.assertNotNull(bookDtoWithoutCategoryIdsActual);
        Assertions.assertEquals(listOfBooksDtoExpected.size(),
                bookDtoWithoutCategoryIdsActual.length);
        Assertions.assertEquals(listOfBooksDtoExpected,
                Arrays.stream(bookDtoWithoutCategoryIdsActual).toList());
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Default Category 2");
        requestDto.setDescription("A default category for books 2");
        return requestDto;
    }

    private CategoryDto toCategoryDto(CreateCategoryRequestDto requestDto) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName(requestDto.getName());
        categoryDto.setDescription(requestDto.getDescription());
        return categoryDto;
    }

    private List<CategoryDto> createListOfCategoriesDto() {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(new CategoryDto().setId(1L)
                .setName("Default Category 1")
                .setDescription("A default category for books 1"));

        categoryDtoList.add(new CategoryDto().setId(2L)
                .setName("Default Category 2")
                .setDescription("A default category for books 2"));
        return categoryDtoList;
    }

    private List<BookDtoWithoutCategoryIds> createListOfBooksDtoWithoutCategoryId() {
        List<BookDtoWithoutCategoryIds> bookDtoWithoutCategoryIds = new ArrayList<>();

        bookDtoWithoutCategoryIds.add(new BookDtoWithoutCategoryIds()
                .setId(2L).setTitle("Book Title 2")
                .setAuthor("Author 2").setIsbn("ISBN-00002")
                .setPrice(BigDecimal.valueOf(6.99)).setDescription("Description for Book 2")
                .setCoverImage("coverImage2.jpg"));

        bookDtoWithoutCategoryIds.add(new BookDtoWithoutCategoryIds()
                .setId(4L).setTitle("Book Title 4")
                .setAuthor("Author 4").setIsbn("ISBN-00004")
                .setPrice(BigDecimal.valueOf(8.99)).setDescription("Description for Book 4")
                .setCoverImage("coverImage4.jpg"));
        return bookDtoWithoutCategoryIds;
    }
}
