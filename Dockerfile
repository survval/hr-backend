# ==== 1) Build stage ====
FROM gradle:8.8-jdk21-alpine AS build
WORKDIR /app

# Copy everything (including build.gradle / build.gradle.kts, settings.gradle(.kts), src, gradlew, etc.)
COPY . .

# Build the Spring Boot fat jar
RUN ./gradlew --no-daemon clean bootJar

# ==== 2) Runtime stage ====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the Gradle stage
COPY --from=build /app/build/libs/*.jar app.jar

# JVM options (tune for small containers)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Spring Boot defaults to port 8080 (or PORT env)
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
