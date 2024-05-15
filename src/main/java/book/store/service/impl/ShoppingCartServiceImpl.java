package book.store.service.impl;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Transactional
    @Override
    public ShoppingCartDto addBookInShoppingCart(Long userId, CartItemRequestDto requestDto) {
        ShoppingCart userShoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = cartItemMapper.toModel(requestDto);

        Optional<CartItem> cartItemByBookId = getCartItemByBookId(userShoppingCart,
                requestDto.getBookId());

        if (cartItemByBookId.isEmpty()) {
            createNewCartItemInShoppingCart(userShoppingCart, cartItem);
            return shoppingCartMapper.toDto(userShoppingCart);
        }

        CartItem cartItemWithoutNewQuantity = cartItemByBookId.orElseThrow(() ->
            new EntityNotFoundException("Can`t find cart item by id: " + requestDto.getBookId()));

        cartItem.setQuantity(cartItem.getQuantity() + cartItemWithoutNewQuantity.getQuantity());
        cartItemRepository.delete(cartItemWithoutNewQuantity);
        createNewCartItemInShoppingCart(userShoppingCart, cartItem);
        return shoppingCartMapper.toDto(userShoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto updateQuantity(Long userId, Long cartItemId,
                                          UpdateCartItemDto cartItemDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId());

        if (cartItem.getShoppingCart().getUser().getId().equals(userId)) {
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Transactional
    @Override
    public void deleteBookById(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId());

        if (cartItem.getShoppingCart().getUser().getId().equals(userId)) {
            cartItemRepository.deleteById(cartItem.getId());
        }
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCartByUserId = getShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find a shopping cart by user id: " + userId));
    }

    private Optional<CartItem> getCartItemByBookId(ShoppingCart shoppingCart, Long bookId) {
        return shoppingCart.getCartItems().stream()
                .filter(cartItemBook -> cartItemBook.getBook().getId().equals(bookId))
                .findFirst();
    }

    private void createNewCartItemInShoppingCart(ShoppingCart shoppingCart, CartItem cartItem) {
        cartItemRepository.save(cartItem);
        shoppingCart.getCartItems().add(cartItem);
    }
}
