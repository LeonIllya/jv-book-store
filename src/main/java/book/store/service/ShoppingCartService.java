package book.store.service;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.User;

public interface ShoppingCartService {
    ShoppingCartDto addCartItemInShoppingCart(Long userId, CartItemRequestDto requestDto);

    ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId,
                                    UpdateCartItemDto cartItemDto);

    void deleteCartItemById(Long cartItemId, Long userId);

    ShoppingCartDto getShoppingCart(Long userId);

    void createShoppingCartForUser(User user);

    CartItemDto getCartItemById(Long cartItemId, Long userId);
}
