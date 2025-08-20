package com.qrcode.orderinglocator.repository;

import com.qrcode.orderinglocator.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    
    Optional<RestaurantTable> findByNumber(Integer number);
    
    boolean existsByNumber(Integer number);
}