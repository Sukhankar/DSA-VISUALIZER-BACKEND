# ==============================================================================
# Build Stage: Compile and Package Spring Boot JAR
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production application JAR without running unit tests (tests are verified in CI)
RUN mvn clean package -DskipTests

# ==============================================================================
# Runtime Stage: Lightweight Temurin JRE Runtime Environment
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user and group for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set user permissions
RUN chown -R spring:spring /app

USER spring

# Expose internal backend application port
EXPOSE 8080

# Configure JVM flags and launch Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
