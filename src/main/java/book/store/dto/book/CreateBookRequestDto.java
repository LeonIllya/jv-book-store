package book.store.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBookRequestDto {
    @NotBlank(message = "Please write the title of the book ")
    @Length(min = 3, max = 50)
    private String title;
    @NotBlank(message = "Please write the author of the book ")
    private String author;
    @NotBlank
    @ISBN
    private String isbn;
    @NotNull(message = "Please write the price of the book ")
    @Length(min = 3)
    @Min(0)
    private BigDecimal price;
    @NotBlank
    private String description;
    @NotBlank
    private String coverImage;
    private Set<Long> categoryIds;
}
