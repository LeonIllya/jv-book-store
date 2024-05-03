package book.store.dto.cartitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @NotNull
    private Long bookId;
    @NotBlank(message = "Please write the name of the book")
    private String bookTitle;
    @NotBlank(message = "Please write quantity of the book")
    private int quantity;
}
