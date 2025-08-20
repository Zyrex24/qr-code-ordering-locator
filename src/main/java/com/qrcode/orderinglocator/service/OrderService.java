package com.qrcode.orderinglocator.service;

import com.qrcode.orderinglocator.dto.order.CreateOrderRequest;
import com.qrcode.orderinglocator.dto.order.OrderResponse;
import com.qrcode.orderinglocator.dto.order.UpdateOrderStatusRequest;
import com.qrcode.orderinglocator.entity.*;
import com.qrcode.orderinglocator.exception.InvalidOrderStatusTransitionException;
import com.qrcode.orderinglocator.exception.ResourceNotFoundException;
import com.qrcode.orderinglocator.repository.*;
import com.qrcode.orderinglocator.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private final OrderStatusChangeRepository orderStatusChangeRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating new order with {} items", request.getItems().size());
        
        // Validate and get user if provided
        User customer = null;
        if (request.getCustomerId() != null) {
            customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));
        }

        // Validate and get table if provided
        RestaurantTable table = null;
        if (request.getTableId() != null) {
            table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + request.getTableId()));
        }

        // Create order
        Order order = Order.builder()
                .customer(customer)
                .table(table)
                .status(Order.OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO) // Will be calculated below
                .build();

        // Create order items and calculate total
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice()) // Snapshot current price
                    .build();

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            if (order.getOrderItems() == null) {
                order.setOrderItems(List.of(orderItem));
            } else {
                order.getOrderItems().add(orderItem);
            }
        }

        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        // Record initial status change
        OrderStatusChange statusChange = OrderStatusChange.builder()
                .order(savedOrder)
                .status(Order.OrderStatus.PENDING)
                .build();
        orderStatusChangeRepository.save(statusChange);

        log.info("Order created successfully with ID: {} and total price: {}", savedOrder.getId(), totalPrice);
        
        return mapOrderToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        log.info("Fetching order with ID: {}", orderId);
        
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Check if user has permission to view this order
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            
            // Customers can only view their own orders
            if (userDetails.getRole() == User.Role.CUSTOMER) {
                if (order.getCustomer() == null || !order.getCustomer().getId().equals(userDetails.getId())) {
                    throw new AccessDeniedException("You can only view your own orders");
                }
            }
            // Cashiers and Admins can view all orders
        }

        return mapOrderToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Long tableId, Order.OrderStatus status, Pageable pageable) {
        log.info("Fetching orders with filters - tableId: {}, status: {}", tableId, status);
        
        Page<Order> orders = orderRepository.findOrdersWithFilters(tableId, status, pageable);
        return orders.map(this::mapOrderToResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("Updating order {} status to {}", orderId, request.getStatus());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Validate status transition
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        Order savedOrder = orderRepository.save(order);

        // Record status change
        OrderStatusChange statusChange = OrderStatusChange.builder()
                .order(savedOrder)
                .status(request.getStatus())
                .build();
        orderStatusChangeRepository.save(statusChange);

        log.info("Order status updated successfully for order ID: {}", orderId);
        
        return mapOrderToResponse(savedOrder);
    }

    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // Define allowed transitions: pending -> in_preparation -> ready -> delivered
        boolean isValidTransition = switch (currentStatus) {
            case PENDING -> newStatus == Order.OrderStatus.IN_PREPARATION;
            case IN_PREPARATION -> newStatus == Order.OrderStatus.READY;
            case READY -> newStatus == Order.OrderStatus.DELIVERED;
            case DELIVERED -> false; // No transitions allowed from delivered
        };

        if (!isValidTransition) {
            throw new InvalidOrderStatusTransitionException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    private OrderResponse mapOrderToResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .tableNumber(order.getTable() != null ? order.getTable().getNumber() : null)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .build();
    }
}