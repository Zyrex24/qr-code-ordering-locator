package com.qrcode.orderinglocator.service;

import com.qrcode.orderinglocator.dto.menu.MenuResponse;
import com.qrcode.orderinglocator.entity.Category;
import com.qrcode.orderinglocator.entity.Product;
import com.qrcode.orderinglocator.entity.RestaurantTable;
import com.qrcode.orderinglocator.entity.Settings;
import com.qrcode.orderinglocator.exception.ResourceNotFoundException;
import com.qrcode.orderinglocator.repository.CategoryRepository;
import com.qrcode.orderinglocator.repository.RestaurantTableRepository;
import com.qrcode.orderinglocator.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final RestaurantTableRepository tableRepository;
    private final SettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    public MenuResponse getMenu(Long tableId) {
        log.info("Fetching menu for table ID: {}", tableId);
        
        // Get table information
        MenuResponse.TableInfo tableInfo = null;
        if (tableId != null) {
            RestaurantTable table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + tableId));
            
            tableInfo = MenuResponse.TableInfo.builder()
                    .id(table.getId())
                    .number(table.getNumber())
                    .build();
        }

        // Get restaurant information
        Settings settings = settingsRepository.findAll().stream().findFirst()
                .orElse(null);
        
        MenuResponse.RestaurantInfo restaurantInfo = null;
        if (settings != null) {
            restaurantInfo = MenuResponse.RestaurantInfo.builder()
                    .name(settings.getName())
                    .logoUrl(settings.getLogoUrl())
                    .address(settings.getAddress())
                    .workingHours(settings.getWorkingHours())
                    .facebookUrl(settings.getFacebookUrl())
                    .whatsappNumber(settings.getWhatsappNumber())
                    .phoneNumber(settings.getPhoneNumber())
                    .build();
        }

        // Get categories with products
        List<Category> categories = categoryRepository.findAllWithProducts();
        
        List<MenuResponse.CategoryResponse> categoryResponses = categories.stream()
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());

        return MenuResponse.builder()
                .table(tableInfo)
                .restaurant(restaurantInfo)
                .categories(categoryResponses)
                .build();
    }

    private MenuResponse.CategoryResponse mapCategoryToResponse(Category category) {
        List<MenuResponse.ProductResponse> productResponses = category.getProducts().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());

        return MenuResponse.CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .products(productResponses)
                .build();
    }

    private MenuResponse.ProductResponse mapProductToResponse(Product product) {
        return MenuResponse.ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .build();
    }
}