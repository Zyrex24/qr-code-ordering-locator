package com.qrcode.orderinglocator.service;

import com.qrcode.orderinglocator.dto.order.CreateOrderRequest;
import com.qrcode.orderinglocator.dto.order.OrderResponse;
import com.qrcode.orderinglocator.dto.order.UpdateOrderStatusRequest;
import com.qrcode.orderinglocator.entity.*;
import com.qrcode.orderinglocator.exception.InvalidOrderStatusTransitionException;
import com.qrcode.orderinglocator.exception.ResourceNotFoundException;
import com.qrcode.orderinglocator.repository.*;
import com.qrcode.orderinglocator.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private OrderStatusChangeRepository orderStatusChangeRepository;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private RestaurantTable table;
    private Product product;
    private Order order;
    private OrderItem orderItem;
    private CreateOrderRequest createOrderRequest;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(User.Role.CUSTOMER)
                .build();

        table = RestaurantTable.builder()
                .id(1L)
                .number(1)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("10.99"))
                .build();

        orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(new BigDecimal("10.99"))
                .build();

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .table(table)
                .totalPrice(new BigDecimal("21.98"))
                .status(Order.OrderStatus.PENDING)
                .orderItems(List.of(orderItem))
                .build();

        orderItem.setOrder(order);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setTableId(1L);
        
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        createOrderRequest.setItems(List.of(itemRequest));

        userDetails = CustomUserDetails.fromUser(customer);
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderStatusChangeRepository.save(any(OrderStatusChange.class))).thenReturn(new OrderStatusChange());

        // Act
        OrderResponse response = orderService.createOrder(createOrderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getTableId()).isEqualTo(1L);
        assertThat(response.getTotalPrice()).isEqualTo(new BigDecimal("21.98"));
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(response.getItems()).hasSize(1);

        verify(userRepository).findById(1L);
        verify(tableRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderStatusChangeRepository).save(any(OrderStatusChange.class));
    }

    @Test
    void createOrder_WithInvalidCustomer_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createOrderRequest.setCustomerId(999L);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with ID: 999");

        verify(userRepository).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WithInvalidProduct_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        createOrderRequest.getItems().get(0).setProductId(999L);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(createOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with ID: 999");

        verify(productRepository).findById(999L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_Success_AsCustomer() {
        // Arrange
        when(orderRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(order));
        
        // Set up security context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        OrderResponse response = orderService.getOrderById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);

        verify(orderRepository).findByIdWithDetails(1L);
    }

    @Test
    void getOrderById_CustomerAccessingOtherOrder_ThrowsAccessDeniedException() {
        // Arrange
        User otherCustomer = User.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .role(User.Role.CUSTOMER)
                .build();
        
        Order otherOrder = Order.builder()
                .id(2L)
                .customer(otherCustomer)
                .status(Order.OrderStatus.PENDING)
                .orderItems(List.of())
                .build();

        when(orderRepository.findByIdWithDetails(2L)).thenReturn(Optional.of(otherOrder));
        
        // Set up security context with different user
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(2L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You can only view your own orders");

        verify(orderRepository).findByIdWithDetails(2L);
    }

    @Test
    void updateOrderStatus_ValidTransition_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderStatusChangeRepository.save(any(OrderStatusChange.class))).thenReturn(new OrderStatusChange());

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(Order.OrderStatus.IN_PREPARATION);

        // Act
        OrderResponse response = orderService.updateOrderStatus(1L, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Order.OrderStatus.IN_PREPARATION);

        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderStatusChangeRepository).save(any(OrderStatusChange.class));
    }

    @Test
    void updateOrderStatus_InvalidTransition_ThrowsException() {
        // Arrange
        order.setStatus(Order.OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(Order.OrderStatus.PENDING);

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, request))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from DELIVERED to PENDING");

        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrders_WithFilters_ReturnsPagedResults() {
        // Arrange
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findOrdersWithFilters(eq(1L), eq(Order.OrderStatus.PENDING), any(Pageable.class)))
                .thenReturn(orderPage);

        // Act
        Page<OrderResponse> response = orderService.getOrders(1L, Order.OrderStatus.PENDING, Pageable.unpaged());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(1L);

        verify(orderRepository).findOrdersWithFilters(eq(1L), eq(Order.OrderStatus.PENDING), any(Pageable.class));
    }

    @Test
    void getOrderById_OrderNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(orderRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with ID: 999");

        verify(orderRepository).findByIdWithDetails(999L);
    }
}