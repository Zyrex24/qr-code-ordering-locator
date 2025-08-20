package com.qrcode.orderinglocator.repository;

import com.qrcode.orderinglocator.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products p ORDER BY c.id")
    List<Category> findAllWithProducts();
    
    Optional<Category> findByName(String name);
}