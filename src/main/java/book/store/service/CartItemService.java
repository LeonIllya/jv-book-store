package book.store.service;

import book.store.dto.cartitem.CartItemDto;

public interface CartItemService {
    CartItemDto getCartItemById(Long cartId, Long userId);
}
