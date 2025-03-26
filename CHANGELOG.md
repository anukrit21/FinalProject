# Changelog

## [1.2.0] - 2023-09-01

### Improved
- Optimized all Dockerfiles with better Maven dependency caching
- Added scripts to standardize Dockerfile updates (`update-dockerfiles.bat` and `update-dockerfiles.sh`)
- Enhanced docker-compose.yml with proper healthchecks for all services
- Fixed dependency conditions for service startup in docker-compose.yml
- Added start_period parameter to healthchecks to allow for longer startup times

## [1.1.0] - 2023-08-25

### Added
- Created Dockerfiles for all microservices with multi-stage builds
- Added infrastructure services (Config Server, Discovery Service)
- Included additional services (Payment, Delivery) in Docker Compose
- Created .dockerignore file for optimized builds
- Enhanced Docker Compose with proper service dependencies and healthchecks
- Updated README with comprehensive Docker documentation

### Changed
- Improved service container configuration with environment variables
- Enhanced network configuration for service discovery
- Set up proper service dependencies for orchestrated startup

## [1.0.0] - 2023-08-18

### Added
- Created Windows batch file (`docker-deploy.bat`) for Docker operations
- Updated README.md with deployment instructions for both Linux and Windows
- Added health check endpoints to services
- Created common health controller template
- Added package name standardization scripts
- Updated Docker Compose configuration for consistent naming

### Changed
- Standardized package names to use `com.demoApp.*` format
- Standardized Maven group IDs to use `com.demoApp`
- Standardized Docker container names to use `demoApp_service` format
- Standardized database names to use `demoApp_service` format
- Updated network name to `demoApp-network`
- Updated README to include naming conventions documentation

### Fixed
- Various inconsistencies in package, container, and database naming
- Improved documentation for health checks
- Ensured consistent naming across all services 