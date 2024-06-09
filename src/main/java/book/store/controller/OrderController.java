package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusOrderDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders management", description = "Endpoint for managing orders")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "create place order", description = "create place order")
    public OrderDto createOrder(@RequestBody @Valid PlaceOrderRequestDto placeOrderRequestDto,
                                    Authentication authentication) {
        User user = getUser(authentication);
        return orderService.getOrderByPlace(user.getId(), placeOrderRequestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get orders", description = "Get all orders")
    public List<OrderDto> getOrderHistory(Authentication authentication, Pageable pageable) {
        User user = getUser(authentication);
        return orderService.getOrderHistory(user.getId(), pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update status", description = "Update order status by order id")
    public OrderDto updateStatusOrder(@RequestBody @Valid UpdateStatusOrderDto requestDto,
                                        @PathVariable @Positive Long orderId) {
        return orderService.updateStatusOrder(orderId, requestDto);
    }

    @GetMapping("{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get order items",
            description = "Get all order items by order id and user id")
    public List<OrderItemDto> findOrderItemsByOrder(@PathVariable @Positive Long orderId,
                                                        Authentication authentication) {
        User user = getUser(authentication);
        return orderService.findOrderItemsByOrder(orderId, user.getId());
    }

    @GetMapping("{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get order item",
            description = "Get order item by id and order id and user id")
    public OrderItemDto findOrderItemById(@PathVariable @Positive Long orderId,
                                            @PathVariable @Positive Long itemId,
                                            Authentication authentication) {
        User user = getUser(authentication);
        return orderService.findOrderItemById(orderId, itemId, user.getId());
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
