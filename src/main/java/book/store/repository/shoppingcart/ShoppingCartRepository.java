package book.store.repository.shoppingcart;

import book.store.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query(value = "SELECT sc FROM ShoppingCart sc JOIN sc.user u WHERE u.id = :userId",
            nativeQuery = true)
    ShoppingCart findByUserId(Long userId);

}
