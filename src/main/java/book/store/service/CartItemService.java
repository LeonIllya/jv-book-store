package book.store.service;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;

public interface CartItemService {
    CartItemDto addBookInShoppingCart(ShoppingCartDto cartDto, CreateCartItemRequestDto requestDto);

    void deleteBookById(Long cartId);

    void updateQuantity(Long cartId, int quantity);
}
