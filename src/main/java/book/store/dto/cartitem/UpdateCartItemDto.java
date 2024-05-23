package book.store.dto.cartitem;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemDto {
    @Positive
    private int quantity;
}
