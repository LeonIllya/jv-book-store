package book.store.service;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusOrderDto;
import book.store.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    OrderDto getOrderByPlace(Long userId, PlaceOrderRequestDto placeOrderRequestDto);

    List<OrderDto> getOrderHistory(Long userId);

    OrderDto updateStatusOrder(Long orderId, UpdateStatusOrderDto updateStatusOrderDto);

    List<OrderItemDto> findOrderItemsByOrder(Long orderId, Long userId);

    OrderItemDto findOrderItemById(Long orderId, Long itemId, Long userId);
}
