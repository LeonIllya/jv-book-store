package book.store.dto.order;

import jakarta.validation.constraints.NotBlank;

public record UpdateStatusOrderDto(@NotBlank String status) {
}
