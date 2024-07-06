package book.store.tests.repository;

import book.store.model.Book;
import book.store.repository.book.BookRepository;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private DataSource dataSource;

    @Test
    @Sql(scripts = "classpath:database/books/add-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/remove-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Find all books by categories id")
    void find_ValidBooks_returnListOfBooks() {
        Long categoryId = 1L;
        List<Book> actual = bookRepository.findAllByCategoryId(categoryId);

        Assertions.assertEquals(2, actual.size());
        Assertions.assertEquals("Author C", actual.get(0).getAuthor());
        Assertions.assertEquals("Author A", actual.get(1).getAuthor());
    }
}
