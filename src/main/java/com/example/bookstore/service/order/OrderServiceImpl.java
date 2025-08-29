package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PlaceOrderRequestDto;
import com.example.bookstore.dto.order.UpdateStatusRequestDto;
import com.example.bookstore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{
    @Override
    public OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto) {
        return null;
    }

    @Override
    public Page<OrderDto> getOrdersHistory(User user, Pageable pageable) {
        return null;
    }

    @Override
    public Page<OrderItemDto> getOrderItems(User user, Long orderId, Pageable pageable) {
        return null;
    }

    @Override
    public OrderItemDto getOrderItemById(User user, Long orderId, Long itemId) {
        return null;
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto) {
        return null;
    }
}
