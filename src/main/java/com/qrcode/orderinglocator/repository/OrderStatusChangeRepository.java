package com.qrcode.orderinglocator.repository;

import com.qrcode.orderinglocator.entity.OrderStatusChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusChangeRepository extends JpaRepository<OrderStatusChange, Long> {
    
    List<OrderStatusChange> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}