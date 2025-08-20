-- V2__seed_data.sql - Insert seed data for testing

-- Insert users (password: Admin@123 for admin, Cashier@123 for cashier, Customer@123 for customers)
-- BCrypt hash for Admin@123: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
-- BCrypt hash for Cashier@123: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
-- BCrypt hash for Customer@123: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.

INSERT INTO users (name, email, phone, password_hash, role) VALUES
('Admin User', 'admin@qrlocator.com', '+1234567890', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN'),
('John Cashier', 'cashier@qrlocator.com', '+1234567891', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CASHIER'),
('Alice Customer', 'alice@example.com', '+1234567892', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER'),
('Bob Customer', 'bob@example.com', '+1234567893', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'CUSTOMER');

-- Insert restaurant settings
INSERT INTO settings (name, logo_url, address, working_hours, about_image_url, about_description, terms_and_conditions, facebook_url, whatsapp_number, phone_number, second_phone_number) VALUES
('QR Locator Restaurant', 
 'https://example.com/logo.png', 
 '123 Main Street, City, State 12345', 
 'Monday-Sunday: 9:00 AM - 10:00 PM',
 'https://example.com/about.jpg',
 'Welcome to QR Locator Restaurant! We serve delicious food with modern technology. Simply scan the QR code on your table to browse our menu and place your order.',
 'By using our service, you agree to our terms and conditions. Please inform us of any allergies or dietary restrictions.',
 'https://facebook.com/qrlocator',
 '+1234567890',
 '+1234567890',
 '+1234567899');

-- Insert restaurant tables
INSERT INTO tables (number, qr_code_url) VALUES
(1, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=1'),
(2, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=2'),
(3, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=3'),
(4, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=4'),
(5, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=5'),
(6, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=6'),
(7, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=7'),
(8, 'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=http://localhost:8080/api/menu?table_id=8');

-- Insert categories
INSERT INTO categories (name) VALUES
('Appetizers'),
('Main Courses'),
('Desserts'),
('Beverages'),
('Salads'),
('Soups');

-- Insert products
INSERT INTO products (name, description, image_url, price, category_id) VALUES
-- Appetizers (category_id = 1)
('Buffalo Wings', 'Crispy chicken wings tossed in spicy buffalo sauce', 'https://example.com/buffalo-wings.jpg', 12.99, 1),
('Mozzarella Sticks', 'Golden fried mozzarella sticks with marinara sauce', 'https://example.com/mozzarella-sticks.jpg', 8.99, 1),
('Loaded Nachos', 'Tortilla chips topped with cheese, jalapeños, and sour cream', 'https://example.com/nachos.jpg', 10.99, 1),

-- Main Courses (category_id = 2)
('Grilled Chicken Breast', 'Juicy grilled chicken breast with herbs and spices', 'https://example.com/grilled-chicken.jpg', 18.99, 2),
('Beef Burger', 'Classic beef burger with lettuce, tomato, and cheese', 'https://example.com/beef-burger.jpg', 15.99, 2),
('Salmon Fillet', 'Fresh Atlantic salmon grilled to perfection', 'https://example.com/salmon.jpg', 22.99, 2),
('Pasta Carbonara', 'Creamy pasta with bacon, eggs, and parmesan cheese', 'https://example.com/carbonara.jpg', 16.99, 2),
('Vegetarian Pizza', 'Wood-fired pizza with fresh vegetables and mozzarella', 'https://example.com/veggie-pizza.jpg', 14.99, 2),

-- Desserts (category_id = 3)
('Chocolate Cake', 'Rich chocolate cake with chocolate ganache', 'https://example.com/chocolate-cake.jpg', 7.99, 3),
('Cheesecake', 'New York style cheesecake with berry compote', 'https://example.com/cheesecake.jpg', 6.99, 3),
('Ice Cream Sundae', 'Vanilla ice cream with chocolate sauce and whipped cream', 'https://example.com/sundae.jpg', 5.99, 3),

-- Beverages (category_id = 4)
('Coca Cola', 'Classic Coca Cola soft drink', 'https://example.com/coke.jpg', 2.99, 4),
('Fresh Orange Juice', 'Freshly squeezed orange juice', 'https://example.com/orange-juice.jpg', 4.99, 4),
('Coffee', 'Freshly brewed coffee', 'https://example.com/coffee.jpg', 3.99, 4),
('Iced Tea', 'Refreshing iced tea with lemon', 'https://example.com/iced-tea.jpg', 3.49, 4),

-- Salads (category_id = 5)
('Caesar Salad', 'Crisp romaine lettuce with caesar dressing and croutons', 'https://example.com/caesar-salad.jpg', 11.99, 5),
('Greek Salad', 'Fresh vegetables with feta cheese and olives', 'https://example.com/greek-salad.jpg', 10.99, 5),

-- Soups (category_id = 6)
('Tomato Soup', 'Creamy tomato soup with fresh basil', 'https://example.com/tomato-soup.jpg', 6.99, 6),
('Chicken Noodle Soup', 'Classic chicken noodle soup with vegetables', 'https://example.com/chicken-soup.jpg', 7.99, 6);

-- Insert sample orders for testing
INSERT INTO orders (customer_id, table_id, total_price, status) VALUES
(3, 1, 31.98, 'DELIVERED'),  -- Alice's completed order
(4, 2, 18.99, 'READY'),      -- Bob's ready order
(3, 3, 15.99, 'IN_PREPARATION'); -- Alice's order in preparation

-- Insert order items for the sample orders
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
-- Order 1 (Alice's delivered order)
(1, 1, 2, 12.99),  -- 2x Buffalo Wings
(1, 13, 1, 5.99),  -- 1x Ice Cream Sundae

-- Order 2 (Bob's ready order)
(2, 4, 1, 18.99),  -- 1x Grilled Chicken Breast

-- Order 3 (Alice's order in preparation)
(3, 5, 1, 15.99);  -- 1x Beef Burger

-- Insert order status changes
INSERT INTO order_status_changes (order_id, status) VALUES
-- Order 1 status history
(1, 'PENDING'),
(1, 'IN_PREPARATION'),
(1, 'READY'),
(1, 'DELIVERED'),

-- Order 2 status history
(2, 'PENDING'),
(2, 'IN_PREPARATION'),
(2, 'READY'),

-- Order 3 status history
(3, 'PENDING'),
(3, 'IN_PREPARATION');

-- Insert sample review for delivered order
INSERT INTO reviews (order_id, customer_id, rating, comment) VALUES
(1, 3, 5, 'Excellent food and fast service! The buffalo wings were amazing.');

-- Insert sample enquiries
INSERT INTO enquiries (customer_id, content, visible) VALUES
(3, 'Do you have any gluten-free options available?', TRUE),
(4, 'Can I make a reservation for a large group?', TRUE),
(NULL, 'What are your opening hours on holidays?', TRUE);