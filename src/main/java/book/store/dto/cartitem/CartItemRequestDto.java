package book.store.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartItemRequestDto {
    @NotBlank(message = "Please write the book id ")
    private Long bookId;
    @Min(1)
    private int quantity;
}
