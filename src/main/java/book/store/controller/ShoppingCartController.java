package book.store.controller;

import book.store.dto.cartitem.CartItemDto;
import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.cartitem.UpdateCartItemDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.User;
import book.store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get an user's cart",
            description = "Receive a certain user's shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = getUser(authentication);
        return shoppingCartService.getShoppingCart(user.getId());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Add cart item to the shopping cart",
            description = "Add cart item to the shopping cart")
    public ShoppingCartDto addCartItem(@RequestBody @Valid CartItemRequestDto cartItemRequestDto,
                                   Authentication authentication) {
        User user = getUser(authentication);
        return shoppingCartService.addCartItemInShoppingCart(user.getId(), cartItemRequestDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update an item cart",
            description = "Update an item cart in the shopping cart")
    public ShoppingCartDto updateCartItemQuantity(Authentication authentication,
                                          @PathVariable @Positive Long cartItemId,
                                          @RequestBody @Valid UpdateCartItemDto item) {
        User user = getUser(authentication);
        return shoppingCartService.updateCartItemQuantity(user.getId(), cartItemId, item);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a cart item",
            description = "Delete a cart item in the shopping cart")
    public void deleteCartItemById(Authentication authentication,
                               @PathVariable @Positive Long cartItemId) {
        User user = getUser(authentication);
        shoppingCartService.deleteCartItemById(cartItemId, user.getId());
    }

    @GetMapping("/cart-items/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public CartItemDto getCartItemByCartId(Authentication authentication,
                                           @PathVariable @Positive Long cartItemId) {
        User user = getUser(authentication);
        return shoppingCartService.getCartItemById(cartItemId, user.getId());
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
