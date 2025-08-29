package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PlaceOrderRequestDto;
import com.example.bookstore.dto.order.UpdateStatusRequestDto;
import com.example.bookstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

public interface OrderService {
    OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto);

    Page<OrderDto> getOrdersHistory(User user, Pageable pageable);

    Page<OrderItemDto> getOrderItems(User user, Long orderId, Pageable pageable);

    OrderItemDto getOrderItemById(User user, Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto);
}
