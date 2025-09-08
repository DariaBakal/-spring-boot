package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.PlaceOrderRequestDto;
import com.example.bookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order oder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Order toModel(PlaceOrderRequestDto requestDto);
}
