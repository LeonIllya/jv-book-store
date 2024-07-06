package book.store.tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final Set<Long> apySet = new HashSet<>();
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    private CreateBookRequestDto requestDto;
    private Book book;

    @BeforeEach
    void setUp() {
        requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sample Book 3");
        requestDto.setAuthor("Author C");
        requestDto.setIsbn("9781122334455");
        requestDto.setPrice(BigDecimal.valueOf(29.99));
        requestDto.setDescription("Yet another sample book description.");
        requestDto.setCoverImage("http://example.com/cover3.jpg");

        apySet.add(1L);
        apySet.add(2L);
        requestDto.setCategoryIds(apySet);

        book = new Book();
        book.setId(1L);
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(Set.of(new Category()));
    }

    @Test
    @DisplayName("Save book with categories in database")
    public void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        BookDto bookDtoResponse = new BookDto();
        bookDtoResponse.setId(book.getId());
        bookDtoResponse.setTitle(book.getTitle());
        bookDtoResponse.setAuthor(book.getTitle());
        bookDtoResponse.setIsbn(book.getIsbn());
        bookDtoResponse.setPrice(book.getPrice());
        bookDtoResponse.setDescription(book.getDescription());
        bookDtoResponse.setCoverImage(book.getCoverImage());
        bookDtoResponse.setCategoryIds(apySet);

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDtoResponse);

        BookDto savedBookDtoExpected = bookService.createBook(requestDto);

        assertEquals(savedBookDtoExpected, bookDtoResponse);
    }

    @Test
    @DisplayName("Save book without categories in database")
    public void save_ValidCreateBookRequestDtoWithoutCategories_ReturnsBookDto() {
        BookDto bookDtoResponse = new BookDto();
        bookDtoResponse.setId(book.getId());
        bookDtoResponse.setTitle(book.getTitle());
        bookDtoResponse.setAuthor(book.getTitle());
        bookDtoResponse.setIsbn(book.getIsbn());
        bookDtoResponse.setPrice(book.getPrice());
        bookDtoResponse.setDescription(book.getDescription());
        bookDtoResponse.setCoverImage(book.getCoverImage());

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDtoResponse);

        BookDto savedBookDtoExpected = bookService.createBook(requestDto);

        assertEquals(savedBookDtoExpected, bookDtoResponse);
    }

    @Test
    @DisplayName("Get book by id from database")
    public void get_ValidBook_ReturnBookDto() {
        BookDto bookDtoResponse = new BookDto();
        bookDtoResponse.setId(book.getId());
        bookDtoResponse.setTitle(book.getTitle());
        bookDtoResponse.setAuthor(book.getTitle());
        bookDtoResponse.setIsbn(book.getIsbn());
        bookDtoResponse.setPrice(book.getPrice());
        bookDtoResponse.setDescription(book.getDescription());
        bookDtoResponse.setCoverImage(book.getCoverImage());

        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDtoResponse);

        BookDto bookByIdExpected = bookService.getBookById(bookId);
        assertEquals(bookDtoResponse, bookByIdExpected);
    }

    @Test
    @DisplayName("Can`t find a book by id from database")
    public void get_InvalidBook_ReturnEntityNotFoundException() {
        Long bookId = 100L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                    () -> bookService.getBookById(bookId)
        );

        String expected = "Can`t find a book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find all books from database")
    public void findAll_ValidBooks_ReturnListOfBooks() {
        Book book2 = createBook();
        Page<Book> bookPage = new PageImpl<>(List.of(book, book2));
        PageRequest defaultPageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(defaultPageable)).thenReturn(bookPage);
        BookDto bookDto1 = bookToDto(book);
        BookDto bookDto2 = bookToDto(book2);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto1, bookDto2);

        List<BookDto> response = bookService.getAll(defaultPageable);

        assertEquals(2, response.size());
        assertEquals(bookDto1, response.get(0));
        assertEquals(bookDto2, response.get(1));
    }

    @Test
    @DisplayName("Update book by id")
    public void update_ValidBook_Success() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Long updatedBookId = 1L;
        Book updatedBook = updateBook(book, createBookRequestDto);
        BookDto bookDtoExcepted = bookToDto(updatedBook);

        when(bookRepository.findById(updatedBookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(bookDtoExcepted);

        BookDto bookDtoResponse = bookService.updateById(updatedBookId, requestDto);
        assertEquals(bookDtoExcepted, bookDtoResponse);
    }

    @Test
    @DisplayName("Can`t update book by id")
    public void update_InvalidBook_ReturnEntityNotFoundException() {
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Long bookId = 100L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                    () -> bookService.updateById(bookId, createBookRequestDto)
        );

        String expected = "Can't find a book by id: " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete book by id")
    public void delete_ValidBook_Success() {
        Long deleteBookId = 1L;

        bookService.deleteById(deleteBookId);

        verify(bookRepository).deleteById(deleteBookId);
    }

    @Test
    @DisplayName("Search books with valid params")
    public void search_ValidBookByParams_ReturnListBookDto() {
        String[] titles = new String[] {"Sample Book 3", "Sample Book 1"};
        String[] authors = new String[] {"Author C", "Author A"};
        String[] isbn = new String[] {"9781122334455", "9781234567897"};
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(titles, authors, isbn);
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parametersDto);

        Book newCreateBook = createBook();
        List<Book> books = List.of(book, newCreateBook);

        BookDto bookDto = bookToDto(book);
        BookDto newBookDto = bookToDto(newCreateBook);

        when(bookSpecificationBuilder.build(parametersDto)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification)).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto, newBookDto);

        List<BookDto> bookDtoExpected = List.of(bookDto, newBookDto);
        List<BookDto> bookDtoActual = bookService.search(parametersDto);

        assertEquals(bookDtoExpected, bookDtoActual);
    }

    @Test
    @DisplayName("Search books with invalid params")
    public void search_InvalidParams_ReturnsEmptyList() {
        String[] titles = new String[] {"InvalidAuthor"};
        String[] authors = new String[] {"InvalidTitle"};
        String[] isbn = new String[] {"1000", "2000"};
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(titles, authors, isbn);
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parametersDto);

        Book newCreateBook = createBook();
        List<Book> books = List.of(book, newCreateBook);

        BookDto bookDto = bookToDto(book);
        BookDto newBookDto = bookToDto(newCreateBook);

        when(bookSpecificationBuilder.build(parametersDto)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification)).thenReturn(books);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto, newBookDto);

        List<BookDto> bookDtoActual = bookService.search(parametersDto);
        assertFalse(bookDtoActual.isEmpty());
    }

    @Test
    @DisplayName("Search books with invalid params")
    public void find_ValidBookWithoutCategories_ReturnListBookDtoWithoutCategoryIds() {
        Long categoryId = 1L;
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = createBookDtoWithoutCategoryIds(book);
        when(bookRepository.findById(categoryId)).thenReturn(Optional.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDtoWithoutCategoryIds);

        List<BookDtoWithoutCategoryIds> bookDtoWithoutCategory = List.of(bookDtoWithoutCategoryIds);
        List<BookDtoWithoutCategoryIds> byCategoryId = bookService.findByCategoryId(categoryId);

        assertEquals(bookDtoWithoutCategory, byCategoryId);
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(2L);
        book.setAuthor("Author A");
        book.setIsbn("9781234567897");
        book.setPrice(BigDecimal.valueOf(50.00));
        book.setTitle("Sample Book 1");
        book.setDescription("This is a sample book description.");
        book.setCoverImage("http://example.com/cover1.jpg");
        book.setCategories(Set.of(new Category()));
        return book;
    }

    private BookDto bookToDto(Book bookToBookDto) {
        BookDto bookDto = new BookDto();
        bookDto.setAuthor(bookToBookDto.getAuthor());
        bookDto.setPrice(bookToBookDto.getPrice());
        bookDto.setTitle(bookToBookDto.getTitle());
        bookDto.setDescription(bookToBookDto.getDescription());
        bookDto.setCoverImage(bookToBookDto.getCoverImage());
        bookDto.setCategoryIds(bookToBookDto.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        return bookDto;
    }

    private CreateBookRequestDto getCreateBookRequestDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sample Book 2");
        requestDto.setAuthor("Author B");
        requestDto.setPrice(BigDecimal.valueOf(24.99));
        requestDto.setIsbn("9789876543210");
        requestDto.setDescription("Another sample book description.");
        requestDto.setCoverImage("http://example.com/cover2.jpg");
        requestDto.setCategoryIds(Set.of(1L, 2L));
        return requestDto;
    }

    private Book updateBook(Book book, CreateBookRequestDto requestDto) {
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setTitle(requestDto.getTitle());
        book.setDescription(requestDto.getDescription());
        book.setPrice(requestDto.getPrice());
        book.setCoverImage(requestDto.getCoverImage());
        book.setCategories(requestDto.getCategoryIds().stream().map(categoryId -> {
            Category category = new Category();
            category.setId(categoryId);
            return category;
        }).collect(Collectors.toSet()));
        return book;
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(Book book) {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setId(book.getId());
        bookDtoWithoutCategoryIds.setAuthor(book.getAuthor());
        bookDtoWithoutCategoryIds.setIsbn(book.getIsbn());
        bookDtoWithoutCategoryIds.setPrice(book.getPrice());
        bookDtoWithoutCategoryIds.setDescription(book.getDescription());
        bookDtoWithoutCategoryIds.setTitle(book.getTitle());
        bookDtoWithoutCategoryIds.setCoverImage(book.getCoverImage());
        return bookDtoWithoutCategoryIds;
    }

}
