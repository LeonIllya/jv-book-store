package book.store.service.impl;

import book.store.dto.orderitem.OrderItemDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderItemMapper;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.repository.order.OrderRepository;
import book.store.repository.orderitem.OrderItemRepository;
import book.store.service.OrderItemService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;

    @Override
    public OrderItemDto getOrderItem(Long orderItemId, Long orderId) {
        OrderItem orderItem = orderItemRepository.findByOrderIdAndOrderItemId(orderId, orderItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order item by id: " + orderItemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public Set<OrderItemDto> getAllOrderItemsByOrderId(Long orderId) {
        Order orderById = getOrderById(orderId);
        return orderById.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Can`t find an order by id: " + orderId));
    }
}
