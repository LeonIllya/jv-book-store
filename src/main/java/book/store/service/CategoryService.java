package book.store.service;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto save(CreateCategoryRequestDto requestDto);

    CategoryDto updateById(Long id, CreateCategoryRequestDto requestDto);

    void deleteById(Long id);
}
