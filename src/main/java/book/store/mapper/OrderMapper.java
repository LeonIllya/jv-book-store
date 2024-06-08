package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.OrderDto;
import book.store.dto.order.PlaceOrderRequestDto;
import book.store.model.CartItem;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {OrderItemMapper.class})
public interface OrderMapper {
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", source = "book.price")
    OrderItem cartItemToOrderItem(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "shoppingCart.user")
    @Mapping(target = "shippingAddress", source = "requestDto.shippingAddress")
    @Mapping(target = "orderItems", source = "shoppingCart.cartItems",
            qualifiedByName = "mapCartItems")
    Order createOrder(ShoppingCart shoppingCart, PlaceOrderRequestDto requestDto);

    @Named("mapCartItems")
    default Set<OrderItem> mapCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::cartItemToOrderItem)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setOrderInOrderItems(@MappingTarget Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
        }
    }
}
