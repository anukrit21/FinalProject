#!/bin/bash

echo "Updating Dockerfiles with optimizations..."

for dir in services/*; do
    if [ -d "$dir" ] && [ -f "$dir/Dockerfile" ]; then
        echo "Optimizing $dir/Dockerfile..."
        
        # Extract the port number from the existing Dockerfile
        PORT=$(grep "EXPOSE" "$dir/Dockerfile" | awk '{print $2}')
        
        # Create the new optimized Dockerfile
        cat > "$dir/Dockerfile" << EOF
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the POM file first for better caching
COPY pom.xml .

# Download dependencies (will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src/ ./src/

# Build the application
RUN mvn clean package -DskipTests

# Second stage: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=docker

# Expose the port
EXPOSE $PORT

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
EOF
    fi
done

echo "All Dockerfiles have been updated successfully." 