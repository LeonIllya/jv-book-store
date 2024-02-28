package book.store.service;

import java.util.List;
import book.store.model.Book;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
