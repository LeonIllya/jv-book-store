package book.store.service.impl;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusOrderDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.CartItem;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.service.OrderService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto getOrderByPlace(Long userId, PlaceOrderRequestDto placeOrderRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

        BigDecimal total = null;
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            total.add(cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        Order order = orderMapper.createOrder(shoppingCart, placeOrderRequestDto);
        shoppingCartRepository.delete(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrderHistory(Long userId, Pageable pageable) {
        List<Order> ordersByUserId = orderRepository.findOrdersByUserId(userId, pageable);
        return ordersByUserId.stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public OrderDto updateStatusOrder(Long orderId, UpdateStatusOrderDto updateStatusOrderDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find an order by id: " + orderId));
        order.setStatus(Order.Status.valueOf(updateStatusOrderDto.status()));
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderItemDto> findOrderItemsByOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId, userId);
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto findOrderItemById(Long orderId, Long itemId, Long userId) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndOrderItemId(orderId, itemId,
                    userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order item by id: " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    private Order getOrderById(Long orderId, Long userId) {
        return orderRepository.findOrderByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order by order id: " + orderId));
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find a shopping cart by user id: " + userId));
    }
}
