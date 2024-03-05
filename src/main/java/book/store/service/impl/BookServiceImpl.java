package book.store.service.impl;

import book.store.dto.BookDto;
import book.store.dto.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.BookRepository;
import book.store.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        bookRepository.createBook(book);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.getBookById(id);
        if (book == null) {
            throw new EntityNotFoundException("Can`t find a book by id: " + id);
        }
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.getAll()
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
