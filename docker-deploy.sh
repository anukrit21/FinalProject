#!/bin/bash

# Colors for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print messages
print_message() {
  echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

print_success() {
  echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] SUCCESS:${NC} $1"
}

print_error() {
  echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR:${NC} $1"
}

print_warning() {
  echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING:${NC} $1"
}

# Function to check if Docker is installed
check_docker() {
  if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker and try again."
    exit 1
  fi

  if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose and try again."
    exit 1
  fi
}

# Make the initialization script executable
make_script_executable() {
  if [[ "$OSTYPE" != "msys"* ]] && [[ "$OSTYPE" != "win"* ]]; then
    print_message "Making initialization script executable..."
    chmod +x docker/postgres/init-multiple-dbs.sh
  fi
}

# Start all services
start_all() {
  print_message "Starting all services..."
  docker-compose up -d
  if [ $? -eq 0 ]; then
    print_success "All services started successfully!"
    print_message "Services are accessible at:"
    print_message "- PostgreSQL: localhost:5432"
    print_message "- pgAdmin: http://localhost:5050"
    print_message "- Config Server: http://localhost:8888"
    print_message "- Discovery Service: http://localhost:8761"
    print_message "- User Service: http://localhost:8085"
    print_message "- Mess Service: http://localhost:8082"
    print_message "- Menu Service: http://localhost:8083"
    print_message "- Owner Service: http://localhost:8086"
    print_message "- Admin Service: http://localhost:8084"
    print_message "- OTP Service: http://localhost:8081"
    print_message "- Auth Service: http://localhost:8089"
    print_message "- Campus Service: http://localhost:8088"
    print_message "- Subscription Service: http://localhost:8087"
    print_message "- Payment Service: http://localhost:8090"
    print_message "- Delivery Service: http://localhost:8091"
  else
    print_error "Failed to start services. Check the logs using 'docker-compose logs'."
  fi
}

# Stop all services
stop_all() {
  print_message "Stopping all services..."
  docker-compose down
  if [ $? -eq 0 ]; then
    print_success "All services stopped successfully!"
  else
    print_error "Failed to stop services."
  fi
}

# Rebuild all services
rebuild_all() {
  print_message "Rebuilding all services..."
  docker-compose build
  if [ $? -eq 0 ]; then
    print_success "All services rebuilt successfully!"
    print_message "You can now start the services with './docker-deploy.sh start'"
  else
    print_error "Failed to rebuild services."
  fi
}

# Reset database (removes volumes)
reset_db() {
  print_warning "This will delete all database data. Are you sure? (y/n)"
  read -r confirm
  if [[ $confirm =~ ^[Yy]$ ]]; then
    print_message "Stopping all services and removing volumes..."
    docker-compose down -v
    if [ $? -eq 0 ]; then
      print_success "All services stopped and volumes removed. Database has been reset."
      print_message "You can now start the services with './docker-deploy.sh start'"
    else
      print_error "Failed to reset database."
    fi
  else
    print_message "Database reset cancelled."
  fi
}

# View logs for a specific service
view_logs() {
  if [ -z "$1" ]; then
    print_error "Please specify a service name."
    print_message "Available services: postgres, pgadmin, config-server, discovery-service, user-service, mess-service, menu-service, owner-service, admin-service, otp-service, auth-service, campus-service, subscription-service, payment-service, delivery-service"
    exit 1
  fi
  
  print_message "Viewing logs for $1..."
  docker-compose logs -f "$1"
}

# Check container status
check_status() {
  print_message "Checking container status..."
  docker-compose ps
}

# Main script execution
check_docker
make_script_executable

case "$1" in
  start)
    start_all
    ;;
  stop)
    stop_all
    ;;
  restart)
    stop_all
    start_all
    ;;
  rebuild)
    rebuild_all
    ;;
  reset-db)
    reset_db
    ;;
  logs)
    view_logs "$2"
    ;;
  status)
    check_status
    ;;
  *)
    print_message "Usage: $0 [start|stop|restart|rebuild|reset-db|logs <service>|status]"
    exit 1
    ;;
esac

exit 0 