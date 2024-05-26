package book.store.repository.orderitem;

import book.store.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o "
            + "WHERE o.id = :orderId AND oi.id = :orderItemId")
    Optional<OrderItem> findByOrderIdAndOrderItemId(Long orderId, Long orderItemId);
}
