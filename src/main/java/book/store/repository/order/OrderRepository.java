package book.store.repository.order;

import book.store.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o FROM Order o WHERE o.user.id = :userId",
            countQuery = "SELECT count(*) FROM Order o WHERE o.user.id = :userId",
            nativeQuery = true)
    List<Order> findOrdersByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findOrderByOrderIdAndUserId(Long orderId, Long userId);
}
