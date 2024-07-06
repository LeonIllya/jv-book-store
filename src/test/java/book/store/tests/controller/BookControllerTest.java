package book.store.tests.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookDto;
import book.store.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private CreateBookRequestDto requestDto;
    private BookDto bookDto;

    @BeforeAll
    static void beforeAll(
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

    @AfterAll
    static void afterAll(
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
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isCreated()).andReturn();

        BookDto bookDtoActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(bookDtoActual);
        EqualsBuilder.reflectionEquals(bookDto, bookDtoActual, "id");
    }

    @Test
    @Sql(scripts = "classpath:database/books/remove-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book by id")
    void updateBookById_ValidRequestDto_returnBookDto() throws Exception {
        CreateBookRequestDto updatedBook = getRequestDto();
        Long id = 1L;
        String jsonRequest = objectMapper.writeValueAsString(updatedBook);
        BookDto bookDtoExpected = toBookDto(updatedBook);

        MvcResult mvcResult = mockMvc.perform(put("books/{id}", id)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto bookDtoActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(bookDtoActual);
        EqualsBuilder.reflectionEquals(bookDtoExpected, bookDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Get all books from database")
    void getAll_GivenAllBooksFromDatabase_ShouldReturnListOfBooks() throws Exception {
        List<BookDto> bookDtoListExpected = new ArrayList<>();
        bookDtoListExpected.add(new BookDto().setId(1L).setTitle("Book Title 1")
                .setAuthor("Author 1").setIsbn("ISBN-00001")
                .setPrice(BigDecimal.valueOf(5.99)).setDescription("Description for Book 1")
                .setCoverImage("coverImage1.jpg").setCategoryIds(Set.of(1L)));

        bookDtoListExpected.add(new BookDto().setId(2L).setTitle("Book Title 2")
                .setAuthor("Author 2").setIsbn("ISBN-00002")
                .setPrice(BigDecimal.valueOf(6.99)).setDescription("Description for Book 2")
                .setCoverImage("coverImage2.jpg").setCategoryIds(Set.of(2L)));

        bookDtoListExpected.add(new BookDto().setId(3L).setTitle("Book Title 3")
                .setAuthor("Author 3").setIsbn("ISBN-00003")
                .setPrice(BigDecimal.valueOf(7.99)).setDescription("Description for Book 3")
                .setCoverImage("coverImage3.jpg").setCategoryIds(Set.of(1L)));

        bookDtoListExpected.add(new BookDto().setId(4L).setTitle("Book Title 4")
                .setAuthor("Author 4").setIsbn("ISBN-00004")
                .setPrice(BigDecimal.valueOf(8.99)).setDescription("Description for Book 4")
                .setCoverImage("coverImage4.jpg").setCategoryIds(Set.of(2L)));

        bookDtoListExpected.add(new BookDto().setId(5L).setTitle("Book Title 5")
                .setAuthor("Author 5").setIsbn("ISBN-00005")
                .setPrice(BigDecimal.valueOf(9.99)).setDescription("Description for Book 5")
                .setCoverImage("coverImage5.jpg").setCategoryIds(Set.of(1L)));

        MvcResult mvcResult = mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] bookDtoListActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(5L, bookDtoListActual.length);
        Assertions.assertEquals(bookDtoListExpected, Arrays.stream(bookDtoListActual).toList());
    }

    private CreateBookRequestDto getRequestDto() {
        CreateBookRequestDto updatedBook = new CreateBookRequestDto();
        updatedBook.setTitle("Book Title 6");
        updatedBook.setAuthor("Author 6");
        updatedBook.setIsbn("ISBN-00006");
        updatedBook.setPrice(BigDecimal.valueOf(100.99));
        updatedBook.setDescription("Description for Book 6");
        updatedBook.setCoverImage("coverImage6.jpg");
        updatedBook.setCategoryIds(Set.of(6L));
        return updatedBook;
    }

    private BookDto toBookDto(CreateBookRequestDto requestDto) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(requestDto.getTitle());
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setPrice(requestDto.getPrice());
        bookDto.setDescription(requestDto.getDescription());
        bookDto.setCoverImage(requestDto.getCoverImage());
        bookDto.setCategoryIds(requestDto.getCategoryIds());
        return bookDto;
    }
}
