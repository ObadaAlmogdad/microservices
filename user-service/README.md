# User Service

## Overview
User Service is a microservice responsible for user management, authentication, and authorization in the LMS (Learning Management System) microservices architecture.

## Features
- User registration and authentication
- Role-based access control (ADMIN, TRAINER, LEARNER)
- JWT token-based authentication
- Password encryption using BCrypt
- User profile management
- Wallet management
- Admin-only trainer creation

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Eureka Server running on port 8761

## Configuration

### Database Setup
1. Create a MySQL database named `user_db`
2. Update database credentials in `application.yml` if needed

### Default Admin User
The service creates a default admin user on startup:
- Email: `admin@lms.com`
- Password: `admin123`
- Role: `ADMIN`

## Running the Service

### Using Maven
```bash
cd user-service
mvn spring-boot:run
```

### Using Docker
```bash
cd user-service
docker build -t user-service .
docker run -p 8081:8081 user-service
```

## API Endpoints

### Public Endpoints (No Authentication)
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/trainers` - Get all trainers

### Authenticated Endpoints
- `GET /api/users/profile` - Get current user profile
- `GET /api/users/{id}` - Get user by ID (ADMIN or own user)
- `PUT /api/users/{id}` - Update user (ADMIN or own user)
- `PATCH /api/users/{id}/wallet` - Update wallet (ADMIN or own user)

### Admin Only Endpoints
- `GET /api/users` - Get all users
- `GET /api/users/learners` - Get all learners
- `DELETE /api/users/{id}` - Delete user
- `POST /api/users/admin/add-trainer` - Add new trainer
- `POST /api/users/admin/add-admin` - Add new admin
- `PUT /api/users/{id}/activate` - Activate user
- `PUT /api/users/{id}/deactivate` - Deactivate user

## Security

### Authentication
- JWT token-based authentication
- Tokens expire after 24 hours
- Tokens include user role information

### Authorization
- Role-based access control using Spring Security
- Method-level security with @PreAuthorize annotations
- Users can only access their own data unless they have ADMIN role

### Password Security
- All passwords are encrypted using BCrypt
- Minimum password length: 6 characters
- Passwords are never returned in API responses

## Error Handling
- Comprehensive error handling with custom exceptions
- Validation errors with detailed field-level messages
- Consistent API response format

## Testing

### Manual Testing
1. Start the service
2. Use the default admin account to login
3. Test various endpoints using tools like Postman or curl

### Example API Calls

#### Register a new user
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@lms.com",
    "password": "admin123"
  }'
```

#### Add trainer (requires admin token)
```bash
curl -X POST http://localhost:8081/api/users/admin/add-trainer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_token>" \
  -d '{
    "fullName": "Trainer Name",
    "email": "trainer@example.com",
    "password": "password123"
  }'
```

#### Add admin (requires admin token)
```bash
curl -X POST http://localhost:8081/api/users/admin/add-admin \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_token>" \
  -d '{
    "fullName": "Admin User",
    "email": "admin2@example.com",
    "password": "password123"
  }'
```

## Monitoring
- Service registers with Eureka Server
- Health check endpoint available
- Logging configured for debugging

## Dependencies
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL Connector
- JWT (jjwt)
- Lombok
- Validation API

## Troubleshooting

### Common Issues
1. **Database Connection**: Ensure MySQL is running and database exists
2. **Eureka Connection**: Ensure Eureka Server is running on port 8761
3. **Port Conflicts**: Ensure port 8081 is available

### Logs
Check application logs for detailed error messages and debugging information. 