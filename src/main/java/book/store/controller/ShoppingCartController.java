package book.store.controller;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CreateCartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.service.CartItemService;
import book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoint for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getUserByEmail(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CartItemDto addBook(@RequestBody CreateCartItemRequestDto cartItemRequestDto,
                               Authentication authentication) {
        String name = authentication.getName();
        return cartItemService.addBookInShoppingCart(shoppingCartService.getUserByEmail(name),
            cartItemRequestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public void updateQuantity(@PathVariable Long cartId, int quantity) {
        cartItemService.updateQuantity(cartId, quantity);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteBookById(@PathVariable Long cartId) {
        cartItemService.deleteBookById(cartId);
    }
}
