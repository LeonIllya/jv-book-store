package book.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotBlank(message = "Please write the name of the category")
    private String name;
    private String description;
}
