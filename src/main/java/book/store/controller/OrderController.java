package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusOrderDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import book.store.service.OrderItemService;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
    private final OrderItemService orderItemService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderDto createOrder(@RequestBody @Valid PlaceOrderRequestDto placeOrderRequestDto,
                                    Authentication authentication) {
        User user = getUser(authentication);
        return orderService.getOrderByPlace(user.getId(), placeOrderRequestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Set<OrderDto> getOrderHistory(Authentication authentication) {
        User user = getUser(authentication);
        return orderService.getOrderHistory(user.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public OrderDto updateStatusOrder(@RequestBody @Valid UpdateStatusOrderDto requestDto,
                                        @PathVariable @Positive Long orderId) {
        return orderService.updateStatusOrder(orderId, requestDto);
    }

    @GetMapping("{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public OrderItemDto getOrderItem(@PathVariable @Positive Long orderItemId,
                                        @PathVariable @Positive Long orderId) {
        return orderItemService.getOrderItem(orderItemId, orderId);
    }

    @GetMapping("{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Set<OrderItemDto> getAllOrderItemsByOrderId(@PathVariable @Positive Long orderId) {
        return orderItemService.getAllOrderItemsByOrderId(orderId);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
