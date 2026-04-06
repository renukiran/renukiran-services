# Multi-stage Dockerfile for the Renukiran Spring Boot app
# Build stage: use a JDK image and the included Gradle wrapper to produce the fat jar
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace
# Copy only the files needed for the build first to leverage layer caching
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle .
COPY src/ src/
# Ensure wrapper is executable and build the fat jar
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# Runtime stage: use a smaller JRE image
FROM eclipse-temurin:17-jre
ARG JAR_FILE=build/libs/renukiran-services-0.0.1-SNAPSHOT.jar
WORKDIR /app
# Copy the jar from the builder stage
COPY --from=builder /workspace/${JAR_FILE} ./app.jar
# Expose the port the app runs on (default configured in app as 8083)
EXPOSE 8080
# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
