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

## Code Examples

### React Integration Example

#### Setting up Axios with Interceptors

```javascript
// src/services/api.js
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding the auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling token expiration
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // If the error is 401 and hasn't already been retried
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Attempt to refresh the token
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post(`${API_URL}/auth/refresh-token`, {
          refreshToken,
        });
        
        const { token } = response.data;
        localStorage.setItem('token', token);
        
        // Retry the original request with the new token
        originalRequest.headers['Authorization'] = `Bearer ${token}`;
        return axios(originalRequest);
      } catch (error) {
        // If refresh fails, redirect to login
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(error);
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

#### Authentication Service

```javascript
// src/services/auth.service.js
import api from './api';

const AuthService = {
  register: (userData) => {
    return api.post('/auth/register', userData);
  },
  
  login: (email, password) => {
    return api.post('/auth/login', { email, password })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('refreshToken', response.data.refreshToken);
        }
        return response.data;
      });
  },
  
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  },
  
  forgotPassword: (email) => {
    return api.post('/auth/forgot-password', { email });
  },
  
  resetPassword: (token, newPassword) => {
    return api.post('/auth/reset-password', { token, newPassword });
  }
};

export default AuthService;
```

#### User Service

```javascript
// src/services/user.service.js
import api from './api';

const UserService = {
  getProfile: () => {
    return api.get('/users/profile');
  },
  
  updateProfile: (userData) => {
    return api.put('/users/profile', userData);
  },
  
  // Admin functions
  getUserById: (id) => {
    return api.get(`/users/${id}`);
  },
  
  updateUser: (id, userData) => {
    return api.put(`/users/${id}`, userData);
  }
};

export default UserService;
```

#### Mess Service

```javascript
// src/services/mess.service.js
import api from './api';

const MessService = {
  getAllMess: () => {
    return api.get('/mess');
  },
  
  getMessById: (id) => {
    return api.get(`/mess/${id}`);
  },
  
  getCurrentMenu: (messId) => {
    return api.get(`/mess/${messId}/menu`);
  },
  
  getMenuForDate: (messId, date) => {
    return api.get(`/mess/${messId}/menu/${date}`);
  }
};

export default MessService;
```

### Angular Integration Example

#### Authentication Interceptor

```typescript
// src/app/interceptors/auth.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, take, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('token');
    
    if (token) {
      request = this.addToken(request, token);
    }

    return next.handle(request).pipe(
      catchError(error => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(request, next);
        }
        
        return throwError(error);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string) {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshToken = localStorage.getItem('refreshToken');
      
      if (refreshToken) {
        return this.authService.refreshToken(refreshToken).pipe(
          switchMap((token: any) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(token);
            return next.handle(this.addToken(request, token));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.authService.logout();
            return throwError(err);
          })
        );
      }
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token != null),
      take(1),
      switchMap(token => {
        return next.handle(this.addToken(request, token));
      })
    );
  }
}
```

#### Authentication Service

```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${API_URL}/auth/login`, {
      email,
      password
    }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('refreshToken', response.refreshToken);
      })
    );
  }

  register(user: any): Observable<any> {
    return this.http.post(`${API_URL}/auth/register`, user);
  }

  refreshToken(refreshToken: string): Observable<any> {
    return this.http.post(`${API_URL}/auth/refresh-token`, {
      refreshToken
    }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${API_URL}/auth/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${API_URL}/auth/reset-password`, {
      token,
      newPassword
    });
  }
}
```

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

## Service Implementation Examples

### Menu Service

```javascript
// src/services/menu.service.js
import api from './api';

const MenuService = {
  getAllMenus: () => {
    return api.get('/menu');
  },
  
  getMenuById: (id) => {
    return api.get(`/menu/${id}`);
  },
  
  getMenuByDate: (date) => {
    return api.get(`/menu/date/${date}`);
  },
  
  getMenuItems: (menuId) => {
    return api.get(`/menu/${menuId}/items`);
  },
  
  createMenu: (menuData) => {
    return api.post('/menu', menuData);
  },
  
  updateMenu: (id, menuData) => {
    return api.put(`/menu/${id}`, menuData);
  },
  
  deleteMenu: (id) => {
    return api.delete(`/menu/${id}`);
  }
};

export default MenuService;
```

### Campus Service

```javascript
// src/services/campus.service.js
import api from './api';

const CampusService = {
  getAllCampuses: () => {
    return api.get('/campus');
  },
  
  getCampusById: (id) => {
    return api.get(`/campus/${id}`);
  },
  
  getCampusBuildings: (campusId) => {
    return api.get(`/campus/${campusId}/buildings`);
  },
  
  getCampusFacilities: (campusId) => {
    return api.get(`/campus/${campusId}/facilities`);
  },
  
  searchCampus: (query) => {
    return api.get(`/campus/search?q=${query}`);
  }
};

export default CampusService;
```

### Delivery Service

```javascript
// src/services/delivery.service.js
import api from './api';

const DeliveryService = {
  createDelivery: (deliveryData) => {
    return api.post('/delivery', deliveryData);
  },
  
  getDeliveryById: (id) => {
    return api.get(`/delivery/${id}`);
  },
  
  getUserDeliveries: () => {
    return api.get('/delivery/user');
  },
  
  cancelDelivery: (id) => {
    return api.put(`/delivery/${id}/cancel`);
  },
  
  trackDelivery: (id) => {
    return api.get(`/delivery/${id}/track`);
  },
  
  updateDeliveryStatus: (id, status) => {
    return api.put(`/delivery/${id}/status`, { status });
  }
};

export default DeliveryService;
```

### Payment Service

```javascript
// src/services/payment.service.js
import api from './api';

const PaymentService = {
  createPayment: (paymentData) => {
    return api.post('/payments/create', paymentData);
  },
  
  getPaymentById: (id) => {
    return api.get(`/payments/${id}`);
  },
  
  getUserPayments: () => {
    return api.get('/payments/user');
  },
  
  getPaymentMethods: () => {
    return api.get('/payments/methods');
  },
  
  processWebhook: (webhookData) => {
    return api.post('/payments/webhook', webhookData);
  }
};

export default PaymentService;
```

### Owner Service

```javascript
// src/services/owner.service.js
import api from './api';

const OwnerService = {
  getProfile: () => {
    return api.get('/owners/profile');
  },
  
  updateProfile: (profileData) => {
    return api.put('/owners/profile', profileData);
  },
  
  submitApplication: (applicationData) => {
    return api.post('/owners/applications', applicationData);
  },
  
  getMessInfo: () => {
    return api.get('/owners/mess');
  },
  
  updateMessInfo: (messData) => {
    return api.put('/owners/mess', messData);
  }
};

export default OwnerService;
```

### Admin Service

```javascript
// src/services/admin.service.js
import api from './api';

const AdminService = {
  getAllUsers: () => {
    return api.get('/admin/users');
  },
  
  getAllOwners: () => {
    return api.get('/admin/owners');
  },
  
  getOwnerApplications: () => {
    return api.get('/admin/applications');
  },
  
  approveApplication: (id) => {
    return api.put(`/admin/applications/${id}/approve`);
  },
  
  rejectApplication: (id) => {
    return api.put(`/admin/applications/${id}/reject`);
  }
};

export default AdminService;
```

### OTP Service

```javascript
// src/services/otp.service.js
import api from './api';

const OtpService = {
  sendOtp: (contact) => {
    return api.post('/otp/send', contact);
  },
  
  verifyOtp: (data) => {
    return api.post('/otp/verify', data);
  },
  
  resendOtp: (contact) => {
    return api.post('/otp/resend', contact);
  }
};

export default OtpService;
```