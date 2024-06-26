package book.store.dto.order;

import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequestDto(
        @NotBlank String shippingAddress) {
}
