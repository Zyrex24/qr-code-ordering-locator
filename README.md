# QR Code Ordering Locator

A production-ready Spring Boot backend for a QR Code-based restaurant ordering system with JWT authentication, role-based access control, and complete order lifecycle management.

## 🚀 Features

- **JWT Authentication** with role-based access control (Admin, Cashier, Customer)
- **Complete Order Lifecycle** management with status transitions
- **QR Code Integration** for table-based ordering
- **PostgreSQL** persistence with Flyway migrations
- **Comprehensive Testing** with JUnit 5, Mockito, and Testcontainers
- **API Documentation** with OpenAPI/Swagger
- **Docker Support** with multi-stage builds
- **CI/CD Pipeline** with GitHub Actions
- **Newman API Testing** for contract validation

## 🏗️ Architecture

### Tech Stack
- **Java 17** with Spring Boot 3.x
- **Maven** for build management
- **PostgreSQL 14+** with Flyway migrations
- **Spring Security** with JWT tokens
- **JUnit 5 + Mockito** for unit testing
- **Testcontainers** for integration testing
- **Docker** for containerization
- **GitHub Actions** for CI/CD

### Database Schema
![ER Diagram](docs/er-diagram.puml)

## 🛠️ Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 14+ (or Docker)

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/Zyrex24/qr-code-ordering-locator.git
   cd qr-code-ordering-locator
   ```

2. **Start PostgreSQL with Docker**
   ```bash
   docker-compose up postgres -d
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build manually
docker build -t qr-ordering-locator .
docker run -p 8080:8080 qr-ordering-locator
```

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Menu Endpoints
- `GET /api/menu` - Get menu with categories and products
- `GET /api/menu?table_id={id}` - Get menu for specific table

### Order Endpoints
- `POST /api/orders` - Create new order (Customer)
- `GET /api/orders/{id}` - Get order details
- `GET /api/orders` - List orders with filters
- `PATCH /api/orders/{id}/status` - Update order status (Cashier/Admin)

### Default Users (from seed data)
- **Admin**: `admin@qrlocator.com` / `Admin@123`
- **Cashier**: `cashier@qrlocator.com` / `Cashier@123`
- **Customer**: `alice@example.com` / `Customer@123`

## 🧪 Testing

### Run All Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# With coverage report
mvn clean verify jacoco:report
```

### API Testing with Postman/Newman
```bash
# Install Newman
npm install -g newman

# Run API tests
newman run postman_collection.json -e postman_environment.json
```

### Test Coverage
- **Target**: ≥80% line coverage
- **Reports**: `target/site/jacoco/index.html`

## 🔒 Security

### JWT Configuration
- **Algorithm**: HS256
- **Expiration**: 1 hour
- **Secret**: Configurable via environment

### Role-Based Access
- **ADMIN**: Full system access
- **CASHIER**: Order management, status updates
- **CUSTOMER**: Order creation, own order viewing

### Password Security
- **Hashing**: BCrypt with salt
- **Validation**: Minimum 8 characters, complexity requirements

## 🚀 Deployment

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/qr_ordering_locator
SPRING_DATASOURCE_USERNAME=qr_user
SPRING_DATASOURCE_PASSWORD=qr_password

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=3600000

# Profile
SPRING_PROFILES_ACTIVE=production
```

### CI/CD Pipeline
The GitHub Actions workflow automatically:
1. Runs unit and integration tests
2. Enforces code coverage thresholds
3. Executes Newman API tests
4. Builds and tests Docker images
5. Reports test results and coverage

## 📊 Monitoring

### Health Checks
- **Application**: `/actuator/health`
- **Database**: Automatic connection validation
- **Docker**: Built-in health checks

### Logging
- **Level**: Configurable per environment
- **Format**: Structured JSON in production
- **Key Events**: Authentication, order lifecycle, errors

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass and coverage is maintained
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- **Issues**: [GitHub Issues](https://github.com/Zyrex24/qr-code-ordering-locator/issues)
- **Email**: support@qrlocator.com
- **Documentation**: [API Docs](http://localhost:8080/swagger-ui.html)