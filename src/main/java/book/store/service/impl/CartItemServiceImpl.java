package book.store.service.impl;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CartItemMapper;
import book.store.model.CartItem;
import book.store.repository.book.BookRepository;
import book.store.repository.cartitem.CartItemRepository;
import book.store.service.CartItemService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public CartItemDto addBookInShoppingCart(ShoppingCartDto cartDto,
                                                CreateCartItemRequestDto requestDto) {
        CartItem cartItem = cartItemMapper.toModel(requestDto);

        cartItem.setBook(bookRepository.findById(requestDto.getBookId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Can`t find a book by id:"
                                + requestDto.getBookId())));
        CartItem saveCartItem = cartItemRepository.save(cartItem);
        cartDto.getCartItems().add(cartItemMapper.toDto(cartItem));
        return cartItemMapper.toDto(saveCartItem);
    }

    @Override
    public void deleteBookById(Long cartId) {
        cartItemRepository.deleteById(cartId);
    }

    @Override
    public void updateQuantity(Long cartId, int quantity) {
        Optional<CartItem> cartById = cartItemRepository.findById(cartId);
        if (cartById.isPresent()) {
            cartById.get().setQuantity(quantity);
            cartItemRepository.save(cartById.get());
            return;
        }
        throw new RuntimeException("Can`t find cart item by id: " + cartId);
    }
}
