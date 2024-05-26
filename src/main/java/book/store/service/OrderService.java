package book.store.service;

import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.dto.order.UpdateStatusOrderDto;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    OrderDto getOrderByPlace(Long userId, PlaceOrderRequestDto placeOrderRequestDto);

    Set<OrderDto> getOrderHistory(Long userId);

    OrderDto updateStatusOrder(Long orderId, UpdateStatusOrderDto updateStatusOrderDto);
}
