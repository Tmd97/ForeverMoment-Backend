# Build Stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Skip tests to speed up build, assuming tests are run in CI/CD
RUN mvn clean package -DskipTests

# Run Stage
# FROM openjdk:17-jdk-slim
FROM maven:3.9.9-eclipse-temurin-17-alpine
WORKDIR /app
# Copy the jar from the core module where the main application resides
COPY --from=build /app/moment_forever_core/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
