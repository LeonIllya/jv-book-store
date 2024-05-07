package book.store.service.impl;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto addBookInShoppingCart(Long userId, CartItemRequestDto requestDto) {
        ShoppingCart userShoppingCart = shoppingCartRepository.getUserById(userId);
        CartItem model = cartItemMapper.toModel(requestDto);
        cartItemRepository.save(model);
        userShoppingCart.getCartItems().add(model);
        return shoppingCartMapper.toDto(userShoppingCart);
    }

    @Override
    public void updateQuantity(String email, Long cartItemId, int quantity) {
        CartItem cartItem = findCartItemById(cartItemId);

        if (cartItem.getShoppingCart().getUser().getEmail().equals(email)) {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    public void deleteBookById(Long cartItemId, String userEmail) {
        CartItem cartItem = findCartItemById(cartItemId);

        if (cartItem.getShoppingCart().getUser().getEmail().equals(userEmail)) {
            cartItemRepository.deleteById(cartItem.getId());
        }
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart byUserId = shoppingCartRepository.getUserById(userId);
        return shoppingCartMapper.toDto(byUserId);
    }

    private CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId).orElseThrow(() ->
            new EntityNotFoundException("Can`t find cart item by id: " + cartItemId));
    }
}
