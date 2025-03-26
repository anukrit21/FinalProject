@echo off
SETLOCAL EnableDelayedExpansion

echo Updating Dockerfiles with optimizations...

FOR /D %%d IN (services\*) DO (
    IF EXIST "%%d\Dockerfile" (
        echo Optimizing %%d\Dockerfile...
        
        type NUL > "%%d\Dockerfile.new"
        (
            echo FROM maven:3.9-eclipse-temurin-21-alpine AS build
            echo WORKDIR /app
            echo.
            echo # Copy the POM file first for better caching
            echo COPY pom.xml .
            echo.
            echo # Download dependencies ^(will be cached if pom.xml doesn't change^)
            echo RUN mvn dependency:go-offline -B
            echo.
            echo # Copy the source code
            echo COPY src/ ./src/
            echo.
            echo # Build the application
            echo RUN mvn clean package -DskipTests
            echo.
            echo # Second stage: runtime
            echo FROM eclipse-temurin:21-jre-alpine
            echo WORKDIR /app
            echo.
            echo # Copy the JAR file from the build stage
            echo COPY --from=build /app/target/*.jar app.jar
            echo.
            echo # Environment variables
            echo ENV SPRING_PROFILES_ACTIVE=docker
            echo.
            echo # Expose the port
        ) > "%%d\Dockerfile.new"
        
        FOR /F "tokens=1* delims=:" %%a IN ('findstr /n "EXPOSE" "%%d\Dockerfile"') DO (
            echo %%b>> "%%d\Dockerfile.new"
        )
        
        (
            echo.
            echo # Run the application
            echo ENTRYPOINT ["java", "-jar", "/app/app.jar"]
        ) >> "%%d\Dockerfile.new"
        
        move /y "%%d\Dockerfile.new" "%%d\Dockerfile" > NUL
    )
)

echo All Dockerfiles have been updated successfully. 