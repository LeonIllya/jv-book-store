package book.store.service.impl;

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
import book.store.repository.user.UserRepository;
import book.store.service.ShoppingCartService;
import java.util.Optional;
import java.util.Set;
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
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ShoppingCartDto addBookInShoppingCart(Long userId, CartItemRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Book with id: %d is not found", requestDto.getBookId())
                ));

        if (!hasUserShoppingCart(userId)) {
            ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

            shoppingCart.getCartItems().stream()
                    .filter(item -> item.getBook().getId().equals(requestDto.getBookId()))
                    .findFirst()
                    .ifPresentOrElse(item -> item.setQuantity(item.getQuantity()
                            + requestDto.getQuantity()),
                        () -> addCartItemToCart(requestDto, book, shoppingCart));
            shoppingCartRepository.save(shoppingCart);
            return shoppingCartMapper.toDto(shoppingCart);
        } else {
            createShoppingCartByUserId(userId);
            ShoppingCart shoppingCartByUserId = getShoppingCartByUserId(userId);
            shoppingCartByUserId.getCartItems().add(createCartItem(book,
                    requestDto.getQuantity(), shoppingCartByUserId));

            shoppingCartRepository.save(shoppingCartByUserId);
            return shoppingCartMapper.toDto(shoppingCartByUserId);
        }
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

    public ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find a shopping cart by user id: " + userId));
    }

    private boolean hasUserShoppingCart(Long userId) {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByUserId(userId);
        return shoppingCart.isEmpty();
    }

    private void createShoppingCartByUserId(Long userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(getUserById(userId));
        shoppingCart.setCartItems(Set.of(new CartItem()));
        shoppingCartRepository.save(shoppingCart);
    }

    private CartItem createCartItem(Book book, int quantity, ShoppingCart shoppingCart) {
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);
        return cartItem;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find a user by id: " + userId));
    }

    private void addCartItemToCart(CartItemRequestDto itemDto, Book book, ShoppingCart cart) {
        CartItem cartItem = cartItemMapper.toModel(itemDto);
        cartItem.setBook(book);
        cart.getCartItems().add(cartItem);
        cartItem.setShoppingCart(cart);
    }
}
