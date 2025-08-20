package com.qrcode.orderinglocator.controller;

import com.qrcode.orderinglocator.dto.order.CreateOrderRequest;
import com.qrcode.orderinglocator.dto.order.OrderResponse;
import com.qrcode.orderinglocator.dto.order.UpdateOrderStatusRequest;
import com.qrcode.orderinglocator.entity.Order;
import com.qrcode.orderinglocator.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with items")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get order details by ID")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get orders", description = "Get orders with optional filters")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @Parameter(description = "Filter by table ID")
            @RequestParam(required = false) Long table_id,
            @Parameter(description = "Filter by order status")
            @RequestParam(required = false) Order.OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse> orders = orderService.getOrders(table_id, status, pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update order status (cashier/admin only)")
    @PreAuthorize("hasRole('CASHIER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}