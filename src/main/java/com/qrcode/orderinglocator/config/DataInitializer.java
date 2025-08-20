package com.qrcode.orderinglocator.config;

import com.qrcode.orderinglocator.entity.*;
import com.qrcode.orderinglocator.entity.User.Role;
import com.qrcode.orderinglocator.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final RestaurantTableRepository tableRepository;
    private final SettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Initializing database with sample data...");
            initializeUsers();
            initializeTables();
            initializeCategories();
            initializeProducts();
            initializeSettings();
            log.info("Database initialization completed!");
        }
    }

    private void initializeUsers() {
        // Admin user
        User admin = new User();
        admin.setName("System Administrator");
        admin.setEmail("admin@qrlocator.com");
        admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        admin.setPhone("+1234567890");
        userRepository.save(admin);

        // Cashier user
        User cashier = new User();
        cashier.setName("John Cashier");
        cashier.setEmail("cashier@qrlocator.com");
        cashier.setPasswordHash(passwordEncoder.encode("Cashier@123"));
        cashier.setRole(Role.CASHIER);
        cashier.setPhone("+1234567891");
        userRepository.save(cashier);

        // Customer user
        User customer = new User();
        customer.setName("Alice Customer");
        customer.setEmail("alice@example.com");
        customer.setPasswordHash(passwordEncoder.encode("Customer@123"));
        customer.setRole(Role.CUSTOMER);
        customer.setPhone("+1234567892");
        userRepository.save(customer);

        log.info("Created {} users", userRepository.count());
    }

    private void initializeTables() {
        for (int i = 1; i <= 10; i++) {
            RestaurantTable table = new RestaurantTable();
            table.setNumber(i);
            table.setQrCodeUrl("https://example.com/qr/table/" + i);
            tableRepository.save(table);
        }
        log.info("Created {} tables", tableRepository.count());
    }

    private void initializeCategories() {
        String[] categoryNames = {
            "Appetizers", "Main Courses", "Desserts", "Beverages", "Salads"
        };

        for (String name : categoryNames) {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
        }
        log.info("Created {} categories", categoryRepository.count());
    }

    private void initializeProducts() {
        Category appetizers = categoryRepository.findByName("Appetizers").orElse(null);
        Category mainCourses = categoryRepository.findByName("Main Courses").orElse(null);
        Category desserts = categoryRepository.findByName("Desserts").orElse(null);
        Category beverages = categoryRepository.findByName("Beverages").orElse(null);

        if (appetizers != null) {
            createProduct("Chicken Wings", "Spicy buffalo wings with ranch dip", new BigDecimal("12.99"), appetizers);
            createProduct("Mozzarella Sticks", "Crispy mozzarella with marinara sauce", new BigDecimal("8.99"), appetizers);
        }

        if (mainCourses != null) {
            createProduct("Grilled Chicken", "Herb-seasoned grilled chicken breast", new BigDecimal("18.99"), mainCourses);
            createProduct("Beef Burger", "Juicy beef patty with lettuce and tomato", new BigDecimal("15.99"), mainCourses);
            createProduct("Fish & Chips", "Beer-battered fish with crispy fries", new BigDecimal("16.99"), mainCourses);
        }

        if (desserts != null) {
            createProduct("Chocolate Cake", "Rich chocolate layer cake", new BigDecimal("6.99"), desserts);
            createProduct("Ice Cream", "Vanilla ice cream with chocolate sauce", new BigDecimal("4.99"), desserts);
        }

        if (beverages != null) {
            createProduct("Coca Cola", "Classic soft drink", new BigDecimal("2.99"), beverages);
            createProduct("Fresh Orange Juice", "Freshly squeezed orange juice", new BigDecimal("3.99"), beverages);
            createProduct("Coffee", "Premium roasted coffee", new BigDecimal("2.49"), beverages);
        }

        log.info("Created {} products", productRepository.count());
    }

    private void createProduct(String name, String description, BigDecimal price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl("https://example.com/images/" + name.toLowerCase().replace(" ", "-") + ".jpg");
        productRepository.save(product);
    }

    private void initializeSettings() {
        Settings settings = new Settings();
        settings.setName("QR Code Restaurant");
        settings.setAddress("123 Main Street, City, State 12345");
        settings.setPhoneNumber("+1234567890");
        settings.setSecondPhoneNumber("+1234567891");
        settings.setWhatsappNumber("+1234567890");
        settings.setWorkingHours("Mon-Sun: 10:00 AM - 10:00 PM");
        settings.setAboutDescription("Welcome to QR Code Restaurant! We serve delicious food with modern technology.");
        settings.setTermsAndConditions("By using our service, you agree to our terms and conditions.");
        settings.setLogoUrl("https://example.com/logo.png");
        settings.setAboutImageUrl("https://example.com/about.jpg");
        settings.setFacebookUrl("https://facebook.com/qrrestaurant");
        settingsRepository.save(settings);

        log.info("Created restaurant settings");
    }
}
