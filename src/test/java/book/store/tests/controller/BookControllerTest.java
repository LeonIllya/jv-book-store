package book.store.tests.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import java.util.Objects;
import java.util.Set;
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
public class BookControllerTest {
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
    @DisplayName("Create a new book")
    void createBook_validRequestDto_Success() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto bookDtoExpected = createBookDto(bookRequestDto);
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(post("/books")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto bookDtoActual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(bookDtoActual);
        EqualsBuilder.reflectionEquals(bookDtoExpected, bookDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update book by id")
    void updateBookById_validRequestDto_returnBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        Long id = 1L;
        BookDto bookDtoExpected = toBookDto(bookRequestDto);
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        BookDto bookDtoActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(bookDtoActual);
        EqualsBuilder.reflectionEquals(bookDtoExpected, bookDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Can`t update book by id")
    void updateBookById_invalidRequestId_returnEntityNotFoundException() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        Long id = 100L;
        String expected = "Can't find a book by id: " + id;
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult mvcResult = mockMvc.perform(put("/books/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String message = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        Assertions.assertEquals(expected, message);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Delete book by id")
    void deleteBookById_validRequestDto_Success() throws Exception {
        CreateBookRequestDto bookRequestDto = getBookRequestDtoFromDB();
        long id = 5;
        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(delete("/books/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get book by id from database")
    void getBookById_validRequestDto_returnBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = getBookRequestDtoFromDB();
        Long id = 5L;
        BookDto bookDtoExpected = createBookDto(bookRequestDto);

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);
        MvcResult result = mockMvc.perform(get("/books/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto bookDtoActual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(bookDtoActual);
        EqualsBuilder.reflectionEquals(bookDtoExpected, bookDtoActual, "id");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Can`t get book by id from database")
    void getBookById_invalidRequestDto_returnEntityNotFoundException() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        Long id = 100L;
        String expected = "Can`t find a book by id: " + id;

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);
        MvcResult result = mockMvc.perform(get("/books/{id}", id)
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String message = Objects.requireNonNull(result.getResolvedException()).getMessage();
        Assertions.assertEquals(expected, message);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Get all books from database")
    void getAll_givenAllBooksFromDatabase_shouldReturnListOfBooks() throws Exception {
        List<BookDto> bookDtoListExpected = createListOfBooksDto();

        MvcResult mvcResult = mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] bookDtoListActual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto[].class);
        Assertions.assertEquals(5L, bookDtoListActual.length);
        Assertions.assertEquals(bookDtoListExpected, Arrays.stream(bookDtoListActual).toList());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Search book by params from database")
    void searchBooks_validParameters_returnListOfBooks() throws Exception {
        List<BookDto> listOfBooksByParametersExpected = getListOfBooksByParameters();

        MvcResult mvcResult = mockMvc.perform(get("/books/search")
                    .param("authors", "Author 1, Author 4")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] bookDtos = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BookDto[].class);
        Assertions.assertNotNull(bookDtos);
        Assertions.assertEquals(listOfBooksByParametersExpected.size(), bookDtos.length);
        Assertions.assertEquals(listOfBooksByParametersExpected, Arrays.stream(bookDtos).toList());
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sample Book 3");
        requestDto.setAuthor("Author C");
        requestDto.setIsbn("9781122334455");
        requestDto.setPrice(BigDecimal.valueOf(29.99));
        requestDto.setDescription("Yet another sample book description.");
        requestDto.setCoverImage("http://example.com/cover3.jpg");
        requestDto.setCategoryIds(Set.of(1L,2L));
        return requestDto;
    }

    private CreateBookRequestDto getBookRequestDtoFromDB() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Book Title 5");
        requestDto.setAuthor("Author 5");
        requestDto.setIsbn("ISBN-00005");
        requestDto.setPrice(BigDecimal.valueOf(9.99));
        requestDto.setDescription("Description for Book 5");
        requestDto.setCoverImage("coverImage5.jpg");
        return requestDto;
    }

    private BookDto createBookDto(CreateBookRequestDto requestDto) {
        BookDto bookDto = new BookDto();
        bookDto.setId(6L);
        bookDto.setTitle(requestDto.getTitle());
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setIsbn(requestDto.getIsbn());
        bookDto.setPrice(requestDto.getPrice());
        bookDto.setDescription(requestDto.getDescription());
        bookDto.setCoverImage(requestDto.getCoverImage());
        return bookDto;
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

    private List<BookDto> createListOfBooksDto() {
        List<BookDto> bookDtoList = new ArrayList<>();
        bookDtoList.add(new BookDto().setId(1L).setTitle("Book Title 1")
                .setAuthor("Author 1").setIsbn("ISBN-00001")
                .setPrice(BigDecimal.valueOf(5.99)).setDescription("Description for Book 1")
                .setCoverImage("coverImage1.jpg").setCategoryIds(Set.of(1L)));

        bookDtoList.add(new BookDto().setId(2L).setTitle("Book Title 2")
                .setAuthor("Author 2").setIsbn("ISBN-00002")
                .setPrice(BigDecimal.valueOf(6.99)).setDescription("Description for Book 2")
                .setCoverImage("coverImage2.jpg").setCategoryIds(Set.of(2L)));

        bookDtoList.add(new BookDto().setId(3L).setTitle("Book Title 3")
                .setAuthor("Author 3").setIsbn("ISBN-00003")
                .setPrice(BigDecimal.valueOf(7.99)).setDescription("Description for Book 3")
                .setCoverImage("coverImage3.jpg").setCategoryIds(Set.of(1L)));

        bookDtoList.add(new BookDto().setId(4L).setTitle("Book Title 4")
                .setAuthor("Author 4").setIsbn("ISBN-00004")
                .setPrice(BigDecimal.valueOf(8.99)).setDescription("Description for Book 4")
                .setCoverImage("coverImage4.jpg").setCategoryIds(Set.of(2L)));

        bookDtoList.add(new BookDto().setId(5L).setTitle("Book Title 5")
                .setAuthor("Author 5").setIsbn("ISBN-00005")
                .setPrice(BigDecimal.valueOf(9.99)).setDescription("Description for Book 5")
                .setCoverImage("coverImage5.jpg").setCategoryIds(Set.of(1L)));
        return bookDtoList;
    }

    private List<BookDto> getListOfBooksByParameters() {
        List<BookDto> bookDtoList = new ArrayList<>();

        bookDtoList.add(new BookDto().setId(1L).setTitle("Book Title 1")
                .setAuthor("Author 1").setIsbn("ISBN-00001")
                .setPrice(BigDecimal.valueOf(5.99)).setDescription("Description for Book 1")
                .setCoverImage("coverImage1.jpg").setCategoryIds(Set.of(1L)));

        bookDtoList.add(new BookDto().setId(4L).setTitle("Book Title 4")
                .setAuthor("Author 4").setIsbn("ISBN-00004")
                .setPrice(BigDecimal.valueOf(8.99)).setDescription("Description for Book 4")
                .setCoverImage("coverImage4.jpg").setCategoryIds(Set.of(2L)));

        return bookDtoList;
    }
}
