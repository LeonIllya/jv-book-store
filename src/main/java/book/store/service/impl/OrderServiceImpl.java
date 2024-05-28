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
import book.store.model.User;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.repository.shoppingcart.ShoppingCartRepository;
import book.store.repository.user.UserRepository;
import book.store.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto getOrderByPlace(Long userId, PlaceOrderRequestDto placeOrderRequestDto) {
        User user = getUserById(userId);

        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);

        BigDecimal total = null;
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            total.add(cartItem.getBook().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        Order order = createOrder(user, total, placeOrderRequestDto.shippingAddress(),
                shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrderHistory(Long userId) {
        Set<Order> ordersByUserId = orderRepository.findOrdersByUserId(userId);
        return ordersByUserId.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

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
        Order orderById = getOrderById(orderId, userId);
        return orderById.getOrderItems().stream()
            .map(orderItemMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public OrderItemDto findOrderItemById(Long orderId, Long itemId, Long userId) {
        Order order = orderRepository.findOrderByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order by id: " + orderId));

        OrderItem orderItem = orderItemRepository.findByOrderIdAndOrderItemId(order.getId(), itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order item by id: " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    private Order getOrderById(Long orderId, Long userId) {
        return orderRepository.findById(orderId).filter(order -> order.getUser().getId()
                    .equals(userId))
            .orElseThrow(() -> new EntityNotFoundException(
                "Can`t find an order by id: " + orderId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Can`t fina a user by id: " + userId));
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find a shopping cart by user id: " + userId));
    }

    private Order createOrder(User user, BigDecimal total,
                                String shippingAddress, ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setTotal(total);

        Set<OrderItem> orderItems = createOrderItems(shoppingCart, order);
        order.setOrderItems(orderItems);

        orderRepository.save(order);
        return order;
    }

    private Set<OrderItem> createOrderItems(ShoppingCart shoppingCart, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());

            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }
        return orderItems;
    }
}
