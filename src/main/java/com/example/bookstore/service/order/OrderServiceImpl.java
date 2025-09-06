package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.OrderItemDto;
import com.example.bookstore.dto.order.PlaceOrderRequestDto;
import com.example.bookstore.dto.order.UpdateStatusRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.InvalidStatusTransitionException;
import com.example.bookstore.exception.OrderProcessingException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.Order.OrderStatus;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.order.OrderRepository;
import com.example.bookstore.repository.order.item.OrderItemRepository;
import com.example.bookstore.repository.shopping.cart.CartItemRepository;
import com.example.bookstore.repository.shopping.cart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.IN_PROGRESS, OrderStatus.CANCELED),
            OrderStatus.IN_PROGRESS, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELED)
    );
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto) {
        ShoppingCart userCart = shoppingCartRepository.findByUserId(user.getId());
        Set<CartItem> cartItems = userCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new OrderProcessingException(
                    "Shopping cart is empty for user with id: " + user.getId());
        }
        Set<OrderItem> orderItems = cartItems.stream()
                .map(orderItemMapper::toOrderItem)
                .collect(Collectors.toSet());

        BigDecimal total = orderItems.stream()
                .map(o -> o.getPrice().multiply(BigDecimal.valueOf(o.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(orderItems);
        order.setTotal(total);
        order.setUser(user);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepository.save(order);
        cartItemRepository.deleteAll(userCart.getCartItems());
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getOrdersHistory(User user, Pageable pageable) {
        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public Page<OrderItemDto> getOrderItems(User user, Long orderId, Pageable pageable) {
        orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Order with id: %s does not exist for the current user".formatted(
                                        orderId)));
        Page<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId, pageable);
        return orderItems.map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getOrderItemById(User user, Long orderId, Long itemId) {
        orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Order with id: %s does not exist for the current user".formatted(
                                        orderId)));
        OrderItem item = orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order #%s item with id: %s".formatted(orderId, itemId)));

        return orderItemMapper.toDto(item);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Order with id: %s does not exist".formatted(orderId)));
        validateStatusChange(order.getStatus(), requestDto.getStatus());
        order.setStatus(requestDto.getStatus());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    private void validateStatusChange(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!VALID_TRANSITIONS.containsKey(currentStatus)
                || !VALID_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new InvalidStatusTransitionException(
                    "Invalid status transition from %s to %s".formatted(currentStatus, newStatus));
        }
    }
}
