package book.store.repository;

import book.store.model.Book;
import java.util.List;

public interface BookRepository {
    Book createBook(Book book);

    Book getBookById(Long id);

    List<Book> getAll();
}
