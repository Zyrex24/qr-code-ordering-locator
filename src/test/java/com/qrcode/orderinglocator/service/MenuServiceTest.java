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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private SettingsRepository settingsRepository;

    @InjectMocks
    private MenuService menuService;

    private RestaurantTable table;
    private Settings settings;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        table = RestaurantTable.builder()
                .id(1L)
                .number(1)
                .qrCodeUrl("http://example.com/qr1")
                .build();

        settings = Settings.builder()
                .id(1L)
                .name("Test Restaurant")
                .logoUrl("http://example.com/logo.png")
                .address("123 Test St")
                .workingHours("9AM-10PM")
                .facebookUrl("http://facebook.com/test")
                .whatsappNumber("+1234567890")
                .phoneNumber("+1234567890")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .imageUrl("http://example.com/product.jpg")
                .price(new BigDecimal("10.99"))
                .build();

        category = Category.builder()
                .id(1L)
                .name("Test Category")
                .products(List.of(product))
                .build();

        product.setCategory(category);
    }

    @Test
    void getMenu_WithValidTableId_ReturnsMenuWithTableInfo() {
        // Arrange
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(settingsRepository.findAll()).thenReturn(List.of(settings));
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(category));

        // Act
        MenuResponse response = menuService.getMenu(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTable()).isNotNull();
        assertThat(response.getTable().getId()).isEqualTo(1L);
        assertThat(response.getTable().getNumber()).isEqualTo(1);
        
        assertThat(response.getRestaurant()).isNotNull();
        assertThat(response.getRestaurant().getName()).isEqualTo("Test Restaurant");
        
        assertThat(response.getCategories()).hasSize(1);
        assertThat(response.getCategories().get(0).getName()).isEqualTo("Test Category");
        assertThat(response.getCategories().get(0).getProducts()).hasSize(1);
        assertThat(response.getCategories().get(0).getProducts().get(0).getName()).isEqualTo("Test Product");

        verify(tableRepository).findById(1L);
        verify(settingsRepository).findAll();
        verify(categoryRepository).findAllWithProducts();
    }

    @Test
    void getMenu_WithNullTableId_ReturnsMenuWithoutTableInfo() {
        // Arrange
        when(settingsRepository.findAll()).thenReturn(List.of(settings));
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(category));

        // Act
        MenuResponse response = menuService.getMenu(null);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTable()).isNull();
        assertThat(response.getRestaurant()).isNotNull();
        assertThat(response.getCategories()).hasSize(1);

        verify(tableRepository, never()).findById(any());
        verify(settingsRepository).findAll();
        verify(categoryRepository).findAllWithProducts();
    }

    @Test
    void getMenu_WithInvalidTableId_ThrowsResourceNotFoundException() {
        // Arrange
        when(tableRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuService.getMenu(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Table not found with ID: 999");

        verify(tableRepository).findById(999L);
        verify(settingsRepository, never()).findAll();
        verify(categoryRepository, never()).findAllWithProducts();
    }

    @Test
    void getMenu_WithNoSettings_ReturnsMenuWithNullRestaurantInfo() {
        // Arrange
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(settingsRepository.findAll()).thenReturn(List.of());
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of(category));

        // Act
        MenuResponse response = menuService.getMenu(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTable()).isNotNull();
        assertThat(response.getRestaurant()).isNull();
        assertThat(response.getCategories()).hasSize(1);

        verify(tableRepository).findById(1L);
        verify(settingsRepository).findAll();
        verify(categoryRepository).findAllWithProducts();
    }

    @Test
    void getMenu_WithEmptyCategories_ReturnsMenuWithEmptyCategories() {
        // Arrange
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        when(settingsRepository.findAll()).thenReturn(List.of(settings));
        when(categoryRepository.findAllWithProducts()).thenReturn(List.of());

        // Act
        MenuResponse response = menuService.getMenu(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTable()).isNotNull();
        assertThat(response.getRestaurant()).isNotNull();
        assertThat(response.getCategories()).isEmpty();

        verify(tableRepository).findById(1L);
        verify(settingsRepository).findAll();
        verify(categoryRepository).findAllWithProducts();
    }
}