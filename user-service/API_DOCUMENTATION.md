# User Service API Documentation

## Overview
This service manages user authentication, authorization, and user management in the LMS microservices system.

## Base URL
```
http://localhost:8081/api/users
```

## Authentication
Most endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### Public Endpoints (No Authentication Required)

#### 1. Register User
- **POST** `/register`
- **Description**: Register a new user (defaults to LEARNER role)
- **Request Body**:
```json
{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
}
```
- **Response**:
```json
{
    "success": true,
    "message": "User registered successfully",
    "data": {
        "id": 1,
        "fullName": "John Doe",
        "email": "john@example.com",
        "role": "LEARNER",
        "active": true,
        "wallet": 100.0
    }
}
```

#### 2. Login
- **POST** `/login`
- **Description**: Authenticate user and get JWT token
- **Request Body**:
```json
{
    "email": "john@example.com",
    "password": "password123"
}
```
- **Response**:
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "email": "john@example.com",
        "fullName": "John Doe",
        "role": "LEARNER",
        "userId": 1
    }
}
```

#### 3. Get All Trainers
- **GET** `/trainers`
- **Description**: Get list of all trainers (public access)
- **Response**:
```json
{
    "success": true,
    "data": [
        {
            "id": 2,
            "fullName": "Trainer Name",
            "email": "trainer@example.com",
            "role": "TRAINER",
            "active": true,
            "wallet": 100.0
        }
    ]
}
```

### Authenticated Endpoints

#### 4. Get Current User Profile
- **GET** `/profile`
- **Description**: Get current user's profile
- **Authentication**: Required
- **Response**: Same as user object without password

#### 5. Get User by ID
- **GET** `/{id}`
- **Description**: Get user by ID (only ADMIN or the user themselves)
- **Authentication**: Required
- **Authorization**: ADMIN role or own user ID

#### 6. Get User by Email
- **GET** `/email/{email}`
- **Description**: Get user by email (only ADMIN or the user themselves)
- **Authentication**: Required
- **Authorization**: ADMIN role or own email

#### 7. Update User
- **PUT** `/{id}`
- **Description**: Update user information (only ADMIN or the user themselves)
- **Authentication**: Required
- **Authorization**: ADMIN role or own user ID
- **Request Body**: User object (password optional)

#### 8. Update Wallet
- **PATCH** `/{id}/wallet?amount=50.0`
- **Description**: Update user's wallet balance
- **Authentication**: Required
- **Authorization**: ADMIN role or own user ID

### Admin Only Endpoints

#### 9. Get All Users
- **GET** `/`
- **Description**: Get all users
- **Authentication**: Required
- **Authorization**: ADMIN role only

#### 10. Get Users by Role
- **GET** `/role/{role}`
- **Description**: Get users by specific role (ADMIN, TRAINER, LEARNER)
- **Authentication**: Required
- **Authorization**: ADMIN role only

#### 11. Get All Learners
- **GET** `/learners`
- **Description**: Get all learners
- **Authentication**: Required
- **Authorization**: ADMIN or TRAINER role

#### 12. Delete User
- **DELETE** `/{id}`
- **Description**: Delete a user
- **Authentication**: Required
- **Authorization**: ADMIN role only

#### 13. Activate User
- **PUT** `/{id}/activate`
- **Description**: Activate a user account
- **Authentication**: Required
- **Authorization**: ADMIN role only

#### 14. Deactivate User
- **PUT** `/{id}/deactivate`
- **Description**: Deactivate a user account
- **Authentication**: Required
- **Authorization**: ADMIN role only

#### 15. Add Trainer
- **POST** `/admin/add-trainer`
- **Description**: Add a new trainer (ADMIN only)
- **Authentication**: Required
- **Authorization**: ADMIN role only
- **Request Body**:
```json
{
    "fullName": "New Trainer",
    "email": "trainer@example.com",
    "password": "password123"
}
```

#### 16. Add Admin
- **POST** `/admin/add-admin`
- **Description**: Add a new admin (ADMIN only)
- **Authentication**: Required
- **Authorization**: ADMIN role only
- **Request Body**:
```json
{
    "fullName": "New Admin",
    "email": "admin2@example.com",
    "password": "password123"
}
```

## User Roles

### ADMIN
- Full access to all endpoints
- Can manage all users
- Can add trainers
- Can activate/deactivate users

### TRAINER
- Can view learners
- Can view their own profile
- Can update their own information

### LEARNER
- Can view their own profile
- Can update their own information
- Can view trainers (public endpoint)

## Error Responses

### Validation Error
```json
{
    "success": false,
    "message": "Validation failed",
    "data": {
        "email": "Email should be valid",
        "password": "Password must be at least 6 characters"
    }
}
```

### Access Denied
```json
{
    "success": false,
    "message": "Access denied: Access is denied"
}
```

### User Not Found
```json
{
    "success": false,
    "message": "User not found with id: 999"
}
```

### Email Already Exists
```json
{
    "success": false,
    "message": "Email already exists: user@example.com"
}
```

## Security Features

1. **Password Encryption**: All passwords are encrypted using BCrypt
2. **JWT Authentication**: Stateless authentication using JWT tokens
3. **Role-based Authorization**: Different access levels based on user roles
4. **Input Validation**: All inputs are validated using Bean Validation
5. **CORS Support**: Configured for cross-origin requests
6. **Method-level Security**: Using @PreAuthorize annotations

## Configuration

The service uses the following configuration in `application.yml`:

```yaml
jwt:
  secret: mySecretKey
  expiration: 86400000  # 24 hours in milliseconds
```

## Database Schema

### User Entity
- `id`: Primary key
- `fullName`: User's full name
- `email`: Unique email address
- `password`: Encrypted password
- `role`: User role (ADMIN, TRAINER, LEARNER)
- `active`: Account status
- `wallet`: User's wallet balance 