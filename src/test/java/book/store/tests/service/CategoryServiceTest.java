package book.store.tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import book.store.dto.category.CategoryDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.category.CategoryRepository;
import book.store.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private CreateCategoryRequestDto requestDto;
    private Category category;

    @BeforeEach
    void setUp() {
        requestDto = new CreateCategoryRequestDto();
        requestDto.setName("Fiction");
        requestDto.setDescription("Fiction books");

        category = new Category();
        category.setId(1L);
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
    }

    @Test
    @DisplayName("Find all categories in database")
    public void findAll_ValidCategories_returnListOfCategory() {
        Category newCategory = createCategory();
        Page<Category> categoryPage = new PageImpl<>(List.of(category, newCategory));
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        CategoryDto categoryDto1 = categoryToDto(category);
        CategoryDto categoryDto2 = categoryToDto(newCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto1, categoryDto2);

        List<CategoryDto> allCategory = categoryService.findAll(pageable);

        assertEquals(2, allCategory.size());
        assertEquals(categoryDto1, allCategory.get(0));
        assertEquals(categoryDto2, allCategory.get(1));
    }

    @Test
    @DisplayName("Get category by id from database")
    public void get_ValidCategory_returnCategoryDto() {
        Long categoryId = 1L;

        CategoryDto categoryDtoResponse = categoryToDto(category);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDtoResponse);

        CategoryDto categoryByIdExpected = categoryService.getCategoryById(categoryId);
        assertEquals(categoryDtoResponse, categoryByIdExpected);
    }

    @Test
    @DisplayName("Can`t find a category by id from database")
    public void get_InvalidCategory_returnEntityNotFoundException() {
        Long categoryId = 100L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );
        String response = "Can`t find a category by id: " + categoryId;
        String actual = exception.getMessage();

        assertEquals(response, actual);
    }

    @Test
    @DisplayName("Save category in database")
    public void save_ValidBook_returnCategoryDto() {
        CategoryDto categoryDtoResponse = categoryToDto(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDtoResponse);

        CategoryDto categoryDtoExpected = categoryService.save(requestDto);

        assertEquals(categoryDtoResponse, categoryDtoExpected);
    }

    @Test
    @DisplayName("Update category by id")
    public void update_ValidCategory_returnCategoryDto() {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Long updateCategoryId = 1L;
        Category updateCategory = updateCategory(category, categoryRequestDto);
        CategoryDto categoryDtoExpected = categoryToDto(updateCategory);

        when(categoryRepository.findById(updateCategoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(updateCategory)).thenReturn(updateCategory);
        when(categoryMapper.toDto(category)).thenReturn(categoryDtoExpected);

        CategoryDto categoryDtoResponse = categoryService.updateById(updateCategoryId, requestDto);

        assertEquals(categoryDtoExpected, categoryDtoResponse);
    }

    @Test
    @DisplayName("Can`t update category by id")
    public void update_InvalidCategory_returnEntityNotFoundException() {
        Long updateCategoryId = 100L;

        when(categoryRepository.findById(updateCategoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                    () -> categoryService.updateById(updateCategoryId, requestDto)
        );

        String expected = "Can`t find a category by id: " + updateCategoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete a category by id")
    public void delete_ValidCategory_Success() {
        Long deleteCategoryId = 1L;

        categoryService.deleteById(deleteCategoryId);

        verify(categoryRepository).deleteById(deleteCategoryId);
    }

    private Category createCategory() {
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Adventures");
        newCategory.setDescription("Adventures books");
        return newCategory;
    }

    private CategoryDto categoryToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto.setName("Educational");
        categoryRequestDto.setDescription("Educational books");
        return categoryRequestDto;
    }

    private Category updateCategory(Category category, CreateCategoryRequestDto requestDto) {
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        return category;
    }
}
