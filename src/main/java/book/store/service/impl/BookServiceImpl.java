package book.store.service.impl;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import book.store.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto createBook(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto requestDto) {
        Book book = bookRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find a book by id: " + id));

        book.setId(id);
        book.setAuthor(requestDto.getAuthor());
        book.setCoverImage(requestDto.getCoverImage());
        book.setDescription(requestDto.getDescription());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setTitle(requestDto.getTitle());

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find a book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
            .stream()
            .map(bookMapper::toDto)
            .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findByCategoryId(Long id) {
        return bookRepository.findAllByCategoryId(id).stream()
            .map(bookMapper::toDtoWithoutCategories)
            .toList();
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification)
            .stream()
            .map(bookMapper::toDto)
            .toList();
    }
}
