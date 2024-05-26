package book.store.service;

import book.store.dto.orderitem.OrderItemDto;
import java.util.Set;

public interface OrderItemService {
    OrderItemDto getOrderItem(Long orderItemId, Long orderId);

    Set<OrderItemDto> getAllOrderItemsByOrderId(Long orderId);
}
