@echo off
SETLOCAL

:: Define colors for terminal output
set "BLUE=[36m"
set "GREEN=[32m"
set "RED=[31m"
set "YELLOW=[33m"
set "NC=[0m"

:: Function to print messages
:print_message
echo %BLUE%[%date% %time%]%NC% %~1
exit /b

:print_success
echo %GREEN%[%date% %time%] SUCCESS:%NC% %~1
exit /b

:print_error
echo %RED%[%date% %time%] ERROR:%NC% %~1
exit /b

:print_warning
echo %YELLOW%[%date% %time%] WARNING:%NC% %~1
exit /b

:: Check if Docker is installed
where docker >nul 2>&1
if %errorlevel% neq 0 (
    call :print_error "Docker is not installed. Please install Docker and try again."
    exit /b 1
)

where docker-compose >nul 2>&1
if %errorlevel% neq 0 (
    call :print_error "Docker Compose is not installed. Please install Docker Compose and try again."
    exit /b 1
)

:: Parse arguments
if "%~1"=="" (
    call :print_message "Usage: %0 [start^|stop^|restart^|rebuild^|reset-db^|logs <service>^|status]"
    exit /b 1
)

if "%~1"=="start" (
    call :start_all
) else if "%~1"=="stop" (
    call :stop_all
) else if "%~1"=="restart" (
    call :stop_all
    call :start_all
) else if "%~1"=="rebuild" (
    call :rebuild_all
) else if "%~1"=="reset-db" (
    call :reset_db
) else if "%~1"=="logs" (
    call :view_logs %2
) else if "%~1"=="status" (
    call :check_status
) else (
    call :print_message "Usage: %0 [start^|stop^|restart^|rebuild^|reset-db^|logs <service>^|status]"
    exit /b 1
)

exit /b 0

:: Start all services
:start_all
call :print_message "Starting all services..."
docker-compose up -d
if %errorlevel% equ 0 (
    call :print_success "All services started successfully!"
    call :print_message "Services are accessible at:"
    call :print_message "- PostgreSQL: localhost:5432"
    call :print_message "- pgAdmin: http://localhost:5050"
    call :print_message "- Config Server: http://localhost:8888"
    call :print_message "- Discovery Service: http://localhost:8761"
    call :print_message "- User Service: http://localhost:8085"
    call :print_message "- Mess Service: http://localhost:8082"
    call :print_message "- Menu Service: http://localhost:8083"
    call :print_message "- Owner Service: http://localhost:8086"
    call :print_message "- Admin Service: http://localhost:8084"
    call :print_message "- OTP Service: http://localhost:8081"
    call :print_message "- Auth Service: http://localhost:8089"
    call :print_message "- Campus Service: http://localhost:8088"
    call :print_message "- Subscription Service: http://localhost:8087"
    call :print_message "- Payment Service: http://localhost:8090"
    call :print_message "- Delivery Service: http://localhost:8091"
) else (
    call :print_error "Failed to start services. Check the logs using 'docker-compose logs'."
)
exit /b

:: Stop all services
:stop_all
call :print_message "Stopping all services..."
docker-compose down
if %errorlevel% equ 0 (
    call :print_success "All services stopped successfully!"
) else (
    call :print_error "Failed to stop services."
)
exit /b

:: Rebuild all services
:rebuild_all
call :print_message "Rebuilding all services..."
docker-compose build
if %errorlevel% equ 0 (
    call :print_success "All services rebuilt successfully!"
    call :print_message "You can now start the services with 'docker-deploy.bat start'"
) else (
    call :print_error "Failed to rebuild services."
)
exit /b

:: Reset database (removes volumes)
:reset_db
call :print_warning "This will delete all database data. Are you sure? (y/n)"
set /p confirm=
if /i "%confirm%"=="y" (
    call :print_message "Stopping all services and removing volumes..."
    docker-compose down -v
    if %errorlevel% equ 0 (
        call :print_success "All services stopped and volumes removed. Database has been reset."
        call :print_message "You can now start the services with 'docker-deploy.bat start'"
    ) else (
        call :print_error "Failed to reset database."
    )
) else (
    call :print_message "Database reset cancelled."
)
exit /b

:: View logs for a specific service
:view_logs
if "%~1"=="" (
    call :print_error "Please specify a service name."
    call :print_message "Available services: postgres, pgadmin, config-server, discovery-service, user-service, mess-service, menu-service, owner-service, admin-service, otp-service, auth-service, campus-service, subscription-service, payment-service, delivery-service"
    exit /b 1
)

call :print_message "Viewing logs for %~1..."
docker-compose logs -f %~1
exit /b

:: Check container status
:check_status
call :print_message "Checking container status..."
docker-compose ps
exit /b 