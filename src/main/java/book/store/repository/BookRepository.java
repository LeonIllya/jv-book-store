package book.store.repository;

import book.store.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book createBook(Book book);

    Optional<Book> getBookById(Long id);

    List<Book> getAll();
}
