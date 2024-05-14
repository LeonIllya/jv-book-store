package book.store.repository.shoppingcart;

import book.store.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserId(Long userId);
}
