package book.store.service;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import jakarta.validation.constraints.Positive;

public interface ShoppingCartService {
    ShoppingCartDto addBookInShoppingCart(Long userId, CartItemRequestDto requestDto);

    void updateQuantity(String cartId, @Positive Long cartItemId, int quantity);

    void deleteBookById(@Positive Long cartItemId, String cartId);

    ShoppingCartDto getShoppingCart(Long userId);
}
