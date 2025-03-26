# DemoApp

This repository contains the microservices for DemoApp.

## Services

- User Service
- Mess Service
- Menu Service
- Owner Service
- Admin Service
- OTP Service
- Authentication Service
- Campus Service
- Subscription Service

## Docker Setup

All services are containerized using Docker. You can run the entire application stack using the provided deployment scripts:

### For Linux/macOS:
```bash
# Make the scripts executable
chmod +x docker-deploy.sh
chmod +x docker/postgres/init-multiple-dbs.sh

# Start all services
./docker-deploy.sh start
```

### For Windows:
```cmd
# Start all services
docker-deploy.bat start
```

This will start:
- Infrastructure Services:
  - PostgreSQL on port 5432
  - pgAdmin on port 5050 (accessible at http://localhost:5050)
  - Config Server on port 8888
  - Discovery Service (Eureka) on port 8761
- Application Services:
  - User Service on port 8085
  - Mess Service on port 8082
  - Menu Service on port 8083
  - Owner Service on port 8086
  - Admin Service on port 8084
  - OTP Service on port 8081
  - Authentication Service on port 8089
  - Campus Service on port 8088
  - Subscription Service on port 8087
  - Payment Service on port 8090
  - Delivery Service on port 8091

### Deployment Commands

The deployment scripts provide several commands:

- `start`: Start all services
- `stop`: Stop all services
- `restart`: Restart all services
- `rebuild`: Rebuild all service images
- `reset-db`: Reset the database data (removes volumes)
- `logs <service>`: View logs for a specific service
- `status`: Check the status of all containers

Examples:
```bash
# View logs for the user service
./docker-deploy.sh logs user-service

# Check status of all containers
./docker-deploy.sh status
```

### Building the Docker Images

If you need to rebuild the Docker images:

```bash
./docker-deploy.sh rebuild
```

This will:
1. Stop all running services
2. Build all Docker images using the Dockerfiles in each service directory
3. Start all services with the newly built images

### Docker Structure

The Docker setup consists of:

1. Individual **Dockerfiles** for each service located in their respective service directories
2. A master **docker-compose.yml** file that orchestrates all services
3. A **.dockerignore** file to optimize build context
4. Initialization scripts for the PostgreSQL database

### Dockerfile Optimizations

All Dockerfiles use a multi-stage build approach with optimized caching of Maven dependencies:
- The first stage builds the application using Maven in a JDK container
- The second stage creates a lightweight runtime image using JRE

Scripts are provided to standardize and update all Dockerfiles:
```bash
# For Linux/macOS
chmod +x update-dockerfiles.sh
./update-dockerfiles.sh

# For Windows
update-dockerfiles.bat
```

### Health Checks

Docker Compose is configured with health checks for all services to ensure proper startup order. The health checks:
- Monitor the `/actuator/health` endpoint of each service
- Have appropriate intervals and retries for resilience
- Include a start period to allow services time to initialize

### Running Individual Services

If you want to run just a specific service:

```bash
# Example: Running only the user service and required infrastructure
docker-compose up -d postgres config-server discovery-service user-service
```

## Database Setup

All services are configured to use PostgreSQL. Database initialization is handled automatically by the Docker setup.

### pgAdmin Access
- URL: http://localhost:5050
- Email: admin@admin.com
- Password: admin

### Connecting to PostgreSQL
- Host: localhost
- Port: 5432
- Username: postgres
- Password: postgres
- Databases: demoApp_user, demoApp_admin, demoApp_mess, etc.

## Running Services Locally

If you prefer to run services without Docker, update the database credentials in `application.properties` if needed.

```bash
# Example: Running the user service
cd services/user
./mvnw spring-boot:run
```

## Service Endpoints

Here are the default ports for each service:

- User Service: http://localhost:8082
- Mess Service: http://localhost:8085
- Menu Service: http://localhost:8083
- Owner Service: http://localhost:8086
- Admin Service: http://localhost:8084
- OTP Service: http://localhost:8081
- Authentication Service: http://localhost:8089
- Campus Service: http://localhost:8088
- Subscription Service: http://localhost:8087
- Payment Service: http://localhost:8090
- Delivery Service: http://localhost:8091
- Config Server: http://localhost:8888
- Discovery Service: http://localhost:8761
- API Gateway: http://localhost:8080
- Kafka UI: http://localhost:8070
- PostgreSQL: localhost:5432
- pgAdmin: http://localhost:5050

## Development Notes

- Use the `update` schema generation strategy during development (`spring.jpa.hibernate.ddl-auto=update`)
- Change to `validate` or `none` for production
- When developing locally without Docker, make sure PostgreSQL is running on your machine

## Troubleshooting

### Docker Issues
- If you encounter permission issues with the init script:
  ```bash
  chmod +x docker/postgres/init-multiple-dbs.sh
  ```

- If a service fails to start, check its logs:
  ```bash
  ./docker-deploy.sh logs user-service
  ```

### Database Issues
- If you need to reset the database:
  ```bash
  ./docker-deploy.sh reset-db
  ``` 

## Health Checks

Each service includes health check endpoints to monitor service status and connectivity. 
These endpoints follow Spring Boot Actuator patterns:

### Available Health Endpoints

- `GET /actuator/health`: Basic health status (UP/DOWN)
- `GET /actuator/health/details`: Detailed health including database connectivity
- `GET /actuator/info`: Service information and build details

### Using Health Checks

For monitoring service health:
```bash
# Check basic health status of user service
curl http://localhost:8085/actuator/health

# Check detailed health with database status
curl http://localhost:8085/actuator/health/details

# Get service information
curl http://localhost:8085/actuator/info
```

### Implementing Health Checks for New Services

A common health controller template is provided in `common-health-controller.java`. 
To add health checks to a service:

1. Copy the controller to your service's controller package
2. Replace `SERVICE_NAME` with your actual service name
3. Ensure your service has the following dependencies:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-jpa</artifactId>
   </dependency>
   ```
4. Add this configuration to your `application.properties`:
   ```properties
   # Expose actuator endpoints
   management.endpoints.web.exposure.include=health,info
   management.endpoint.health.show-details=always
   ```

## Development Tools

### Package Standardization

The project provides scripts to standardize package names across all services to use `com.demoApp.*` consistently:

#### For Windows:
```cmd
# Run the package standardization script
update-packages.bat
```

#### For Linux/macOS:
```bash
# Make the script executable
chmod +x update-packages.sh

# Run the package standardization script
./update-packages.sh
```

The scripts:
1. Create backups of all modified files in a `backup` directory
2. Update any `com.demoapp.*` package declarations to `com.demoApp.*` in Java files
3. Update any `<groupId>com.demoapp</groupId>` to `<groupId>com.demoApp</groupId>` in pom.xml files
4. Preserve all other code and formatting

After running the scripts, you should rebuild all services to ensure the changes are properly applied. 

### Naming Conventions

The project follows these naming conventions:

1. **Package Names**: All packages follow the `com.demoApp.*` format with a capital 'A' in demoApp
2. **Maven Group IDs**: All Maven projects use `com.demoApp` as the group ID
3. **Database Names**: Databases follow the `demoApp_service` format (e.g., `demoApp_user`)
4. **Container Names**: Docker containers follow the `demoApp_service` format (e.g., `demoApp_postgres`)
5. **Network Names**: Docker networks use the `demoApp-network` format

These conventions are standardized across the project for consistency. When adding new services or features, please adhere to these naming patterns.

## Project History

For a detailed record of changes made to the project, please refer to the [CHANGELOG.md](CHANGELOG.md) file.

## Frontend Integration

The DemoApp backend is designed to work with any modern frontend framework (React, Angular, Vue, etc.). The API Gateway serves as the single entry point for all frontend requests.

### Connection Setup

1. **API Gateway**: All frontend requests should be directed to the API Gateway at `http://localhost:8080/api/{service}/**` which will route to the appropriate microservice.

2. **CORS Configuration**: Cross-Origin Resource Sharing is configured to allow requests from:
   - http://localhost:3000 (React default)
   - http://localhost:4200 (Angular default)
   - http://localhost:8080 (Vue default)
   - Custom domain (set via FRONTEND_URL environment variable)

3. **Authentication**: Frontend must include JWT tokens in the Authorization header:
   ```
   Authorization: Bearer {jwt_token}
   ```

### Service Endpoints

| Service | Base Path | Description |
|---------|-----------|-------------|
| User Service | `/api/users/**` | User profile management |
| Auth Service | `/api/auth/**` | Authentication and authorization |
| Owner Service | `/api/owners/**` | Mess owner management |
| Mess Service | `/api/mess/**` | Mess details and menu management |
| OTP Service | `/api/otp/**` | OTP generation and verification |
| Subscription Service | `/api/subscriptions/**` | Subscription plan management |
| Payment Service | `/api/payments/**` | Payment processing |
| Admin Service | `/api/admin/**` | Admin functions |

### Example Frontend Integration

Example React code to fetch user data:

```javascript
async function fetchUserProfile() {
  const token = localStorage.getItem('jwt_token');
  
  try {
    const response = await fetch('http://localhost:8080/api/users/profile', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (!response.ok) throw new Error('Failed to fetch profile');
    
    const userData = await response.json();
    return userData;
  } catch (error) {
    console.error('Error fetching user profile:', error);
    throw error;
  }
}
```

### Development Workflow

1. Start the backend services using the docker deployment script:
   ```
   ./docker-deploy.sh start
   ```
   or on Windows:
   ```
   docker-deploy.bat start
   ```

2. Run your frontend application in development mode (typically on a different port)

3. The frontend will connect to the backend through the API Gateway on port 8080

## Service Architecture

The DemoApp consists of the following microservices:

| Service | Port | Description |
|---------|------|-------------|
| User Service | 8082 | Manages user profiles and authentication details |
| Auth Service | 8089 | Handles authentication and authorization |
| Owner Service | 8090 | Manages mess owner profiles and operations |
| Mess Service | 8085 | Manages mess details and basic operations |
| Menu Service | 8083 | Handles menu creation and management |
| OTP Service | 8081 | Provides OTP generation and verification |
| Subscription Service | 8087 | Manages user subscriptions to mess services |
| Payment Service | 8088 | Handles payment processing and tracking |
| Admin Service | 8084 | Provides admin dashboard and management tools |
| Campus Service | 8088 | Manages campus information and facilities |
| Delivery Service | 8091 | Handles food delivery tracking and management |
| Config Server | 8888 | Centralized configuration management |
| Discovery Service | 8761 | Service registry and discovery (Eureka) |
| API Gateway | 8080 | Single entry point for all client requests |

Each service is containerized and can be deployed independently. Communication between services happens through:
1. RESTful API calls
2. Asynchronous messaging via Kafka
3. Service discovery through Eureka
"# FinalProject" 
"# DemoApp" 
"# DemoApp" 
"# DemoApp" 
"# FinalProject" 
