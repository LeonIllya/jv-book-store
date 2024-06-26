package book.store.service.impl;

import book.store.dto.cartitem.CartItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemMapper cartItemMapper;
    private final ShoppingCartServiceImpl shoppingCartService;

    @Override
    public CartItemDto getCartItemById(Long cartId, Long userId) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByUserId(userId);

        CartItem cartItem = getCartItemByShoppingCart(shoppingCart, cartId);

        return cartItemMapper.toDto(cartItem);
    }

    private CartItem getCartItemByShoppingCart(ShoppingCart shoppingCart, Long cartId) {
        return shoppingCart.getCartItems().stream()
                .filter(cart -> cart.getId().equals(cartId))
                .findFirst().orElseThrow(() ->
                    new EntityNotFoundException("Can`t find cart by id: " + cartId));
    }
}
