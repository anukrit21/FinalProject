# Frontend Integration Guide for DemoApp

This guide provides detailed instructions for integrating frontend applications with the DemoApp backend microservices.

## Architecture Overview

The DemoApp backend consists of several microservices that can be accessed through a central API Gateway:

```
Frontend App (React/Angular/Vue) --> API Gateway --> Microservices
```

The API Gateway handles:
- Authentication via JWT tokens
- Request routing to appropriate services
- Cross-Origin Resource Sharing (CORS)

## Connection Setup

### Base URL

All API requests should be directed to the API Gateway at:

```
http://localhost:8080/api/{service}/**
```

In production, this would be replaced with your domain name.

### Authentication Flow

1. **Registration**: New users can register via `/api/auth/register`
2. **Login**: Users authenticate via `/api/auth/login` to receive a JWT token
3. **Using the token**: Include the token in the Authorization header for all secured endpoints

## API Endpoints Reference

The following endpoints are available through the API Gateway:

### Authentication Service

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get token
- `POST /api/auth/refresh-token` - Refresh an expired token
- `POST /api/auth/forgot-password` - Initiate password reset
- `POST /api/auth/reset-password` - Complete password reset

### User Service

- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users/{id}` - Get user by ID (admin only)
- `PUT /api/users/{id}` - Update user (admin only)

### Mess Service

- `GET /api/mess` - List all mess facilities
- `GET /api/mess/{id}` - Get mess details
- `GET /api/mess/{id}/menu` - Get current menu
- `GET /api/mess/{id}/menu/{date}` - Get menu for a specific date

### Subscription Service

- `GET /api/subscriptions/plans` - List available subscription plans
- `POST /api/subscriptions` - Create a new subscription
- `GET /api/subscriptions` - List user's subscriptions
- `PUT /api/subscriptions/{id}` - Update subscription
- `DELETE /api/subscriptions/{id}` - Cancel subscription

### Menu Service

- `GET /api/menu` - Get all menus
- `GET /api/menu/{id}` - Get menu by ID
- `GET /api/menu/date/{date}` - Get menu by date
- `GET /api/menu/{id}/items` - Get menu items
- `POST /api/menu` - Create new menu
- `PUT /api/menu/{id}` - Update menu
- `DELETE /api/menu/{id}` - Delete menu

### Campus Service

- `GET /api/campus` - Get all campuses
- `GET /api/campus/{id}` - Get campus by ID
- `GET /api/campus/{id}/buildings` - Get campus buildings
- `GET /api/campus/{id}/facilities` - Get campus facilities
- `GET /api/campus/search` - Search campuses

### Delivery Service

- `POST /api/delivery` - Create new delivery
- `GET /api/delivery/{id}` - Get delivery by ID
- `GET /api/delivery/user` - Get user deliveries
- `PUT /api/delivery/{id}/cancel` - Cancel delivery
- `GET /api/delivery/{id}/track` - Track delivery
- `PUT /api/delivery/{id}/status` - Update delivery status

### Payment Service

- `POST /api/payments/create` - Create payment intent
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/user` - Get user payments
- `POST /api/payments/webhook` - Payment webhook for third-party providers
- `GET /api/payments/methods` - Get payment methods

### Owner Service

- `GET /api/owners/profile` - Get owner profile
- `PUT /api/owners/profile` - Update owner profile
- `POST /api/owners/applications` - Submit owner application
- `GET /api/owners/mess` - Get owner's mess info
- `PUT /api/owners/mess` - Update owner's mess info

### Admin Service

- `GET /api/admin/users` - Get all users (admin only)
- `GET /api/admin/owners` - Get all owners (admin only)
- `GET /api/admin/applications` - Get owner applications (admin only)
- `PUT /api/admin/applications/{id}/approve` - Approve owner application
- `PUT /api/admin/applications/{id}/reject` - Reject owner application

### OTP Service

- `POST /api/otp/send` - Send OTP to email or phone
- `POST /api/otp/verify` - Verify OTP
- `POST /api/otp/resend` - Resend OTP


## Testing the Integration

1. Start the backend:
   ```
   ./docker-deploy.sh start
   ```
   or on Windows:
   ```
   docker-deploy.bat start
   ```

2. Start your frontend application in development mode:
   ```
   npm start
   ```

3. Try accessing an authenticated endpoint. If you receive a 401 status, make sure you've:
   - Successfully logged in
   - Properly stored the JWT token
   - Included the token in your Authorization header

## Troubleshooting

### Common Issues

1. **CORS Errors**: If you see CORS errors in the console, verify:
   - Your frontend is running on an allowed origin
   - You're using the proper HTTP methods
   - You're including the correct headers

2. **Authentication Failures**: If you can't authenticate:
   - Check the token format (must be `Bearer {token}`)
   - Ensure the token hasn't expired
   - Verify you're using the correct credentials

3. **API Gateway Connection Issues**: If you can't connect to the API Gateway:
   - Confirm the API Gateway is running (`docker ps`)
   - Check the API Gateway logs for errors
   - Verify the URL you're using is correct

## Security Considerations

1. Always use HTTPS in production environments
2. Store tokens in secure storage (HttpOnly cookies are recommended for production)
3. Implement proper token expiration and refresh mechanisms
4. Validate all user inputs on both client and server sides