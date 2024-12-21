package book.store.service.impl;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.book.BookRepository;
import book.store.repository.cartitem.CartItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.ShoppingCartService;
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
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCartByUserId = getShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Transactional
    @Override
    public ShoppingCartDto addCartItemInShoppingCart(Long userId, CartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Book with id: %d is not found", requestDto.getBookId())
                ));

        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(requestDto.getBookId()))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(item.getQuantity()
                        + requestDto.getQuantity()),
                    () -> addCartItemToCart(requestDto, book, shoppingCart));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);

    }

    @Transactional
    @Override
    public ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId,
                                          UpdateCartItemDto cartItemDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId()).orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a cart item in shopping cart: " + cartItemId));
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Transactional
    @Override
    public void deleteCartItemById(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId()).orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a cart item in shopping cart: " + cartItemId));
        cartItemRepository.deleteById(cartItem.getId());
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public CartItemDto getCartItemById(Long cartItemId, Long userId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(cartItemId,
                shoppingCart.getId()).orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find a cart item in shopping cart: " + cartItemId));
        return cartItemMapper.toDto(cartItem);
    }

    public ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find a shopping cart by user id: " + userId));
    }

    private void addCartItemToCart(CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.getCartItems().add(cartItem);
        cartItem.setShoppingCart(cart);
    }
}
