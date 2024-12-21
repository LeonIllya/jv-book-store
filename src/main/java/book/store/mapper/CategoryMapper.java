package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto requestDto);

    @Named("categoriesById")
    default Set<Category> categoriesByIds(Set<Long> categories) {
        return categories.stream()
            .map(Category::new)
            .collect(Collectors.toSet());
    }
}
