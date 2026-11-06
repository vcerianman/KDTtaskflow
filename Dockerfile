# ==========================================
# Stage 1: Build the application using Gradle
# ==========================================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Fix Windows CRLF line endings for gradlew and ensure executable permission
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Download dependencies (cached unless dependencies change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build the executable JAR (skip tests during container build)
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# ==========================================
# Stage 2: Minimal runtime image
# ==========================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create a non-privileged user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/Taskflow-*.jar app.jar

# Expose Spring Boot server port
EXPOSE 8080

# Run the Spring Boot application with container-aware JVM settings
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
