package com.qrcode.orderinglocator.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {
    
    private TableInfo table;
    private RestaurantInfo restaurant;
    private List<CategoryResponse> categories;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableInfo {
        private Long id;
        private Integer number;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantInfo {
        private String name;
        private String logoUrl;
        private String address;
        private String workingHours;
        private String facebookUrl;
        private String whatsappNumber;
        private String phoneNumber;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private List<ProductResponse> products;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private BigDecimal price;
    }
}