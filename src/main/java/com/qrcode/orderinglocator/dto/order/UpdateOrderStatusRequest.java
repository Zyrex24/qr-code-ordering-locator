package com.qrcode.orderinglocator.dto.order;

import com.qrcode.orderinglocator.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Status is required")
    private Order.OrderStatus status;
}