package book.store.service;

import book.store.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getUserByEmail(String email);
}
