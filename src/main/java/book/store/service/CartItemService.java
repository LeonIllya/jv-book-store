package book.store.service;

import book.store.dto.cartitem.CartItemDto;
import book.store.model.ShoppingCart;

public interface CartItemService {
    CartItemDto getCartItemById(Long cartId, ShoppingCart shoppingCart);
}
