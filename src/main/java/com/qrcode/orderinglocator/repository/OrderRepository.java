package com.qrcode.orderinglocator.repository;

import com.qrcode.orderinglocator.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.table WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByTableId(Long tableId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.table WHERE (:tableId IS NULL OR o.table.id = :tableId) AND (:status IS NULL OR o.status = :status) ORDER BY o.createdAt DESC")
    Page<Order> findOrdersWithFilters(@Param("tableId") Long tableId, @Param("status") Order.OrderStatus status, Pageable pageable);
}