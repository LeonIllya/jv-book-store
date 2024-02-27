package book.store.repository;

import java.util.List;
import book.store.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
