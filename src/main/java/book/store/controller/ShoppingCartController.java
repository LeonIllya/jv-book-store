package book.store.controller;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.service.CartItemService;
import book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoint for managing shopping cart")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCart(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto addBook(@RequestBody @Valid CartItemRequestDto cartItemRequestDto,
                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookInShoppingCart(user.getId(), cartItemRequestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public ShoppingCartDto updateQuantity(Authentication authentication,
                                            @PathVariable @Positive Long cartItemId,
                                            @RequestBody @Valid UpdateCartItemDto item) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateQuantity(user.getId(), cartItemId, item);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteBookById(Authentication authentication,
                                @PathVariable @Positive Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.deleteBookById(cartItemId, user.getId());
    }

    @GetMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public CartItemDto getCartItemByCartId(Authentication authentication,
                                            @PathVariable @Positive Long cartItemId) {
        ShoppingCart shoppingCart = (ShoppingCart) authentication.getPrincipal();
        return cartItemService.getCartItemById(cartItemId, shoppingCart);
    }
}
