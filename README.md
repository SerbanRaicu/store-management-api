# Store Management System

Spring Boot REST API for inventory management with JWT authentication and role-based access control.

## Features
- **Secure Authentication**: JWT token-based authentication with 24-hour token expiry
- **Role-Based Access Control (RBAC)**: Three hierarchical roles with different permission levels
- **Product Management**: CRUD operations with search, filtering, and pagination
- **User Management**: Admin-controlled user registration and role assignment
- **Data Validation**: Comprehensive input validation on all endpoints
- **Error Handling**: Consistent error responses with meaningful messages
- **In-Memory Database**: H2 database with sample data for quick testing

## Requirements
- Java 17+
- Maven 3.6+

## Setup

```bash
# Clone the repository
git clone <repository-url>
cd store-management

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Application runs on `http://localhost:8080`

## Default Users

| Username | Password | Role | Access Level |
|----------|----------|------|--------------|
| admin | admin123 | ADMIN | Full system access |
| manager | manager123 | MANAGER | Manage products |
| employee | employee123 | EMPLOYEE | View products only |

## Role-Based Access Control (RBAC)

The application implements a hierarchical RBAC system:

- **ADMIN**: Complete access to all endpoints (products + users)
- **MANAGER**: Can create, update products but cannot delete or manage users
- **EMPLOYEE**: Read-only access to product endpoints

Access control is enforced using Spring Security's `@PreAuthorize` annotations at the controller method level, providing fine-grained authorization for each endpoint.

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - Register new user

### Product Management
- `GET /api/products` - Get all products (paginated)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?name={name}` - Search products
- `GET /api/products/category/{category}` - Filter by category
- `POST /api/products` - Create product (Manager/Admin)
- `PUT /api/products/{id}` - Update product (Manager/Admin)
- `DELETE /api/products/{id}` - Delete product (Admin only)

### User Management (Admin only)
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}/role` - Update user role
- `PUT /api/users/{id}/enable` - Enable user
- `PUT /api/users/{id}/disable` - Disable user

## Usage Example

1. **Login to get JWT token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Response:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ADMIN",
    "message": "Login successful"
}
```

2. **Use token for authenticated requests:**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <your-token>"
```

## Project Structure
```
src/main/java/com/ing/store_management/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── security/       # JWT & authentication
├── exception/      # Error handling
└── config/         # App configuration
```

## Technologies
- **Backend**: Java 17, Spring Boot 3.x
- **Security**: Spring Security, JWT
- **Database**: H2 (in-memory)
- **Build**: Maven
- **Testing**: JUnit 5, Mockito

## Testing
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Sample Data
The application loads sample products on startup. Check the H2 console at `/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`).

## Error Handling
All errors return a consistent JSON response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "PRODUCT_NOT_FOUND",
  "message": "Product not found with ID: 123",
  "path": "/api/products/123"
}
```