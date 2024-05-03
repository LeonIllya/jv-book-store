package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    ShoppingCart toModel(ShoppingCartDto shoppingCartDto);
}
