package book.store.service;

import book.store.dto.BookDto;
import book.store.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    List<BookDto> getAll();

    BookDto updateById(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);
}
