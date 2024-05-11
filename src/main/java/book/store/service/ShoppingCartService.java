package book.store.service;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import jakarta.validation.constraints.Positive;

public interface ShoppingCartService {
    ShoppingCartDto addBookInShoppingCart(Long userId, CartItemRequestDto requestDto);

    ShoppingCartDto updateQuantity(Long userId, @Positive Long cartItemId,
                                    UpdateCartItemDto cartItemDto);

    void deleteBookById(@Positive Long cartItemId, Long userId);

    ShoppingCartDto getShoppingCart(Long userId);
}
