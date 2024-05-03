package book.store.service.impl;

import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.mapper.ShoppingCartMapper;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper cartMapper;

    @Override
    public ShoppingCartDto getUserByEmail(String email) {
        return cartMapper.toDto(shoppingCartRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Can`t find user by email: " + email)));
    }
}
