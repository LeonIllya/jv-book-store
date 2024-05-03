package book.store.repository.shoppingcart;

import book.store.model.ShoppingCart;
import book.store.validation.Email;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query(value = "SELECT sc.user FROM ShoppingCart sc JOIN sc.user u WHERE u.email = :email",
            nativeQuery = true)
    Optional<ShoppingCart> findUserByEmail(@Email String email);
}
