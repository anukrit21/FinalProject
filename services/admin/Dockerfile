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
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 